package com.a0100019.mypat.data.room.item

import com.a0100019.mypat.data.room.pat.Pat

fun getItemInitialData(): List<Item> {
    return listOf(
        Item( name = "작은 그림자 1", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 2", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 3", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 4", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 5", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 6", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 7", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 8", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 9", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "작은 그림자 10", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 1", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 2", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 3", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 4", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 5", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 6", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 7", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 8", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 9", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "큰 그림자 10", date = "1", url = "item/shadow.json", minFloat = 0.1f, sizeFloat = 0.1f),
        //20
        Item( name = "비행기", url = "item/airplane.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "로봇 머리", url = "item/ai_robot.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "가을 나무", url = "item/autumn.png", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "박쥐", url = "item/bat.json", minFloat = 0.1f, sizeFloat = 0.1f), //1
        Item( name = "무서운 해골", url = "item/bloody_skull.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "회전목마", url = "item/carousel.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "산타 모자", url = "item/christmas_hat.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "크리스마스 양말", url = "item/christmas_socks.json", minFloat = 0.1f, sizeFloat = 0.1f),
        //10
        Item( name = "크리스마스 나무", url = "item/christmas_tree.json", minFloat = 0.13f, sizeFloat = 0.13f),
        Item( name = "크리스마스 화환", url = "item/christmas_wreath.json", minFloat = 0.07f, sizeFloat = 0.07f),
        Item( name = "묘비 귀신", url = "item/tombstone_pumpkin.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "드래곤 깃발", url = "item/dragon_flag.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "처녀 귀신", url = "item/scary_ghost.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "화난 젤리", url = "item/angry_jelly.json", minFloat = 0.1f, sizeFloat = 0.1f), //1
        Item( name = "깜짝 거미", url = "item/halloween_spider.json", minFloat = 0.1f, sizeFloat = 0.1f),
        //20
        Item( name = "좀비 손", url = "item/zombie_hand.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "해피 할로윈", url = "item/halloween_text.json", minFloat = 0.15f, sizeFloat = 0.15f),
        Item( name = "귀신 가방", url = "item/ghost_bag.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "모닥불", url = "item/campfire.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "삐에로", url = "item/pierrot.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "할로윈 고양이", url = "item/kitty_halloween.json", minFloat = 0.1f, sizeFloat = 0.1f), //1
        Item( name = "대한민국 깃발", url = "item/korea_flag.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "깜짝 호박", url = "item/surprise_pumpkin.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "우체통", url = "item/mailbox.png", minFloat = 0.1f, sizeFloat = 0.1f),
        //30
        Item( name = "메리 크리스마스", url = "item/merry_christmas_text.json", minFloat = 0.15f, sizeFloat = 0.15f),
        Item( name = "겨울 장식 1", url = "item/winter_decoration_1.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "공포 물병", url = "item/scary_potion.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "고양이 스프", url = "item/cat_soup.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "게으른 사슴", url = "item/lazy_deer.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "산타 먼지", url = "item/santa_dust.json", minFloat = 0.1f, sizeFloat = 0.1f), //1
        Item( name = "배", url = "item/ship.json", minFloat = 0.15f, sizeFloat = 0.15f),
        Item( name = "프랑켄", url = "item/frankenstein.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "우주선", url = "item/spaceship.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "그루터기 분쇄기", url = "item/stump_grinder.json", minFloat = 0.1f, sizeFloat = 0.1f),
        //40
        Item( name = "택시", url = "item/taxi.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "묘비", url = "item/tombstone.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "귀신 발자국", url = "item/ghost_footprints.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "그루터기", url = "item/trunk.png", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "회전초", url = "item/tumble_weed.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "눈알", url = "item/watching_you.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "cpu", url = "item/cpu.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "깜짝 손", url = "item/surprise_hand.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item( name = "햄버거 기계", url = "item/burger_machine.json", minFloat = 0.1f, sizeFloat = 0.1f),
        Item(name = "신의 손", url = "pat/god_hand.json", minFloat = 0.12f, sizeFloat = 0.12f),
        Item(name = "새 떼", url = "pat/bird_flock.json", minFloat = 0.1f, sizeFloat = 0.1f),
        //50


    )
}
