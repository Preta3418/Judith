package com.judtih.judith_management_system.domain.board.enums;

/**
 * What kind of content an attachment holds. Decides which fields are populated
 * on PostAttachment/CommentAttachment and how the frontend renders it.
 */
public enum ContentType {
    FILE,   // uploaded to S3 — fileUrl/fileName/fileSize populated; downloads via server proxy
    URL,    // external web link — linkUrl populated; no S3 upload; opens in new tab
    AUDIO   // uploaded to S3 (mp3 etc.) — fileUrl populated; frontend renders an <audio> player
}
