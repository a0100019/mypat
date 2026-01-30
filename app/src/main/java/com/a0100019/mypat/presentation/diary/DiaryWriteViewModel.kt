package com.a0100019.mypat.presentation.diary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.data.room.photo.PhotoDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.world.WorldDao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.io.FileOutputStream
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryDao: DiaryDao,
    private val userDao: UserDao,
    private val worldDao: WorldDao,
    private val patDao: PatDao,
    private val itemDao: ItemDao,
    private val letterDao: LetterDao,
    private val areaDao: AreaDao,
    private val photoDao: PhotoDao
) : ViewModel(), ContainerHost<DiaryWriteState, DiaryWriteSideEffect> {

    override val container: Container<DiaryWriteState, DiaryWriteSideEffect> = container(
        initialState = DiaryWriteState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(DiaryWriteSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    init {
        loadData()
    }

    fun loadData() = intent {

        // 1. suspend로 바로 가져오는 유저 정보
        val userDataList = userDao.getAllUserData()
        val userDataEtc2Value = userDao.getValueById("etc2")
        val allDiaryData = diaryDao.getAllDiaryData()
        val photoDataList = photoDao.getPhotosByDate(userDataEtc2Value)

        if(!userDataEtc2Value.startsWith("0")){
            //0있으면 하루 미션아닌 일기?
            val clickDiaryData = allDiaryData.find { it.date == userDataEtc2Value }
            if (clickDiaryData!!.state == "대기") {
                reduce {
                    state.copy(
                        firstWrite = true,
                        writeDiaryData = Diary(
                            id = clickDiaryData.id,
                            date = clickDiaryData.date,
                            state = "완료",
                            contents = "",
                            emotion = "emotion/smile.png"
                        )
                    )
                }
            } else {
                reduce {
                    state.copy(
                        firstWrite = false,
                        writeDiaryData = clickDiaryData
                    )
                }
            }
        } else {
            //하루미션 아닌 일기

            val lastId = allDiaryData.maxOfOrNull { it.id } ?: 0   // 리스트 비어도 안전

            val newId = if (lastId < 10000) 10000 else lastId + 1

            reduce {
                state.copy(
                    firstWrite = true,
                    writeDiaryData = Diary(
                        id = newId,
                        date = userDataEtc2Value.drop(1),
                        state = "완료",
                        contents = "",
                        emotion = "emotion/smile.png"
                    )
                )
            }
        }

        reduce {
            state.copy(
                dialogState = "",
                writeFinish = false,
                userDataList = userDataList,
                photoDataList = photoDataList
            )
        }

    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        val isAvailable = capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )

        Log.d("NetworkCheck", "Is Network Available: $isAvailable") // 로그 추가
        return isAvailable
    }

    fun handleImageSelection(context: Context, uri: Uri) = intent {
        // 로딩 시작
        reduce { state.copy(isPhotoLoading = true) }

        if (!isNetworkAvailable(context)) {
            postSideEffect(DiaryWriteSideEffect.Toast("인터넷 연결을 확인해주세요."))
            reduce { state.copy(isPhotoLoading = false) } // 종료
            return@intent
        }

        val localPath = withContext(Dispatchers.IO) {
            saveImageToInternalStorage(context, uri)
        }

        if (localPath != null) {
            uploadToFirebase(localPath) { firebaseUrl ->
                // 콜백 안에서 다시 intent 블록을 열어 상태 변경
                intent {
                    if (firebaseUrl != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val photoEntry = Photo(
                                date = state.writeDiaryData.date,
                                localPath = localPath,
                                firebaseUrl = firebaseUrl,
                                isSynced = true
                            )
                            photoDao.insert(photoEntry)
                            val updatedPhotos = photoDao.getPhotosByDate(state.writeDiaryData.date)

                            intent {
                                reduce { state.copy(
                                    photoDataList = updatedPhotos,
                                    isPhotoLoading = false // 성공 시 종료
                                )}
                            }
                        }
                    } else {
                        File(localPath).delete()
                        postSideEffect(DiaryWriteSideEffect.Toast("업로드 실패"))
                        reduce { state.copy(isPhotoLoading = false) } // 실패 시 종료
                    }
                }
            }
        } else {
            postSideEffect(DiaryWriteSideEffect.Toast("저장 오류"))
            reduce { state.copy(isPhotoLoading = false) } // 종료
        }
    }

    private fun uploadToFirebase(localPath: String, onComplete: (String?) -> Unit) {
        val user = Firebase.auth.currentUser ?: return onComplete(null)

        val file = File(localPath)
        if (!file.exists()) return onComplete(null)

        val storageRef = Firebase.storage.reference
        // 보안을 위해 파일명 앞에 UID를 섞거나 랜덤값을 추가하는 것이 좋습니다.
        val imageRef = storageRef.child("users/${user.uid}/${file.name}")

        // 파일 읽기 -> XOR 뒤섞기 -> 업로드
        try {
            val compressedBytes = file.readBytes()
            val scrambledBytes = togglePrivacy(compressedBytes)

            imageRef.putBytes(scrambledBytes)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        onComplete(uri.toString())
                    }
                }
                .addOnFailureListener {
                    onComplete(null)
                }
        } catch (e: Exception) {
            onComplete(null)
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val fileName = "haru_photo_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            // 1. InputStream으로 비트맵 불러오기
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // 2. 파일 출력 스트림 준비
                FileOutputStream(file).use { outputStream ->
                    // 3. 압축하기 (JPEG, 품질 70~80% 권장)
                    // 품질을 100에서 80으로만 낮춰도 용량이 획기적으로 줄어듭니다.
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun togglePrivacy(data: ByteArray): ByteArray {
        val key = 0xAF.toByte()
        return ByteArray(data.size) { i -> (data[i].toInt() xor key.toInt()).toByte() }
    }

    fun deleteImage(photo: Photo) = intent {
        // 1. 비동기 작업으로 파일과 DB 데이터 삭제
        val isDeleted = withContext(Dispatchers.IO) {
            try {
                // (1) 내부 저장소에서 실제 파일 삭제
                val file = File(photo.localPath)
                if (file.exists()) {
                    file.delete()
                }

                // (2) DB에서 해당 포토 엔티티 삭제
                photoDao.delete(photo) // DAO에 delete 메서드가 있다고 가정합니다.
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        if (isDeleted) {
            // 2. 최신 리스트로 UI 상태 갱신
            val updatedList = photoDao.getPhotosByDate(state.writeDiaryData.date)
            reduce {
                state.copy(
                    photoDataList = updatedList
                )
            }
        } else {
            // 실패 시 에러 처리 (선택 사항)
            // postSideEffect(DiaryWriteSideEffect.ShowToast("사진 삭제에 실패했습니다."))
        }

    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                dialogState = "",
            )
        }
    }

    fun onDiaryFinishClick() = intent {
        println("내용 길이: ${state.writeDiaryData.contents.length}")

        if(state.writeDiaryData.contents.isNotEmpty()){

            //보상
            if(state.firstWrite && state.writeDiaryData.id < 10000){
                Log.e("DiaryViewModel", state.userDataList.find { it.id == "money" }!!.value)
                userDao.update(
                    id = "money",
                    value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 3).toString()
                )
                postSideEffect(DiaryWriteSideEffect.Toast("+3 햇살"))
            }

            diaryDao.update(state.writeDiaryData)
            reduce {
                state.copy(
                    writeFinish = true
                )
            }

        }
    }

    @OptIn(OrbitExperimental::class)
    fun onContentsTextChange(contentsText: String) = blockingIntent {

        reduce {
            state.copy(writeDiaryData = state.writeDiaryData.copy(contents = contentsText))
        }

        if (contentsText.length > 0) {
            reduce {
                state.copy(
                    writePossible = true,
                )
            }
        } else {
            postSideEffect(DiaryWriteSideEffect.Toast("10자 이상 입력해주세요"))
            reduce {
                state.copy(
                    writePossible = false,
                )
            }
        }
    }

    fun onDialogStateChange(string: String) = intent {
        reduce {
            state.copy(dialogState = string)
        }
    }

    fun emotionChangeClick(emotion: String) = intent {
        val newWriteDiaryData = state.writeDiaryData
        newWriteDiaryData.emotion = emotion
        reduce {
            state.copy(
                writeDiaryData = newWriteDiaryData,
                dialogState = ""
            )
        }
    }

    fun clickPhotoChange(path: String) = intent {
        reduce {
            state.copy(
                clickPhoto = path
            )
        }
    }

}

@Immutable
data class DiaryWriteState(
    val userDataList: List<User> = emptyList(),
    val diaryDataList: List<Diary> = emptyList(),
    val diaryFilterDataList: List<Diary> = emptyList(),
    val photoDataList: List<Photo> = emptyList(),

    val clickPhoto: String = "",
    val writeDiaryData: Diary = Diary(date = "", contents = "", emotion = ""),
    val writePossible: Boolean = false,
    val searchText: String = "",
    val dialogState: String = "",
    val emotionFilter: String = "emotion/allEmotion.png",
    val firstWrite: Boolean = true,
    val writeFinish: Boolean = false,
    val isPhotoLoading: Boolean = false, // 로딩 상태 추가

)

//상태와 관련없는 것
sealed interface DiaryWriteSideEffect{
    class Toast(val message:String): DiaryWriteSideEffect

}