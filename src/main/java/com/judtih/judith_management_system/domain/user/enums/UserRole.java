package com.judtih.judith_management_system.domain.user.enums;

import java.util.Set;

public enum UserRole {
    //full access members
    LEADER, // 학회장
    PRODUCER, // 연출
    SUB_PRODUCER, //조연출
    PLANNER, // 기획

    //normal members
    ACTOR, //배우
    STAFF, //스태프
    SOUND_OPERATOR, //음향 오퍼레이터
    LIGHT_OPERATOR, //조명 오퍼레이터
    SOUND_DESIGN, //음향 디자인
    LIGHT_DESIGN, //조명 디자인
    IMAGE_DESIGN, //인쇄 디자인
    STAGE_DESIGN; //무대 디자인

    public static final Set<UserRole> FULL_ACCESS_ROLES = Set.of(
            UserRole.LEADER, UserRole.PRODUCER, UserRole.SUB_PRODUCER, UserRole.PLANNER
    );
}
