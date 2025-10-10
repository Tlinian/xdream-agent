package com.xdream.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean first;
  private boolean last;
  private boolean empty;

  public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
    int totalPages = (int) Math.ceil((double) totalElements / size);
    return PageResponse.<T>builder()
        .content(content)
        .page(page)
        .size(size)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .first(page == 0)
        .last(page >= totalPages - 1)
        .empty(content == null || content.isEmpty())
        .build();
  }
}
