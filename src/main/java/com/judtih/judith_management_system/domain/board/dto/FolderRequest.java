package com.judtih.judith_management_system.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Create/rename folder payload. Season+department come from the URL path. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FolderRequest {

    private String name;
}
