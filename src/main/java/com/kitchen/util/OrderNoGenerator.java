package com.kitchen.util;

import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderNoGenerator {

    private final AtomicInteger counter = new AtomicInteger(1);

    public synchronized String generate() {
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        int seq = counter.getAndIncrement();
        if (seq > 9999) {
            counter.set(1);
            seq = 1;
        }
        return String.format("ORD%s%04d", dateStr, seq);
    }
}
