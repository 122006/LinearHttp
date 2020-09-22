package com.by122006.linearhttp.analyse.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor(staticName = "of")
public  class ResultBox {
    final String result;
    final int httpCode;
}
