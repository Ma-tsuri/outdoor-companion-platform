package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.server.dto.BookingDTO;
import com.outdoor.server.entity.Booking;
import com.outdoor.server.vo.BookingVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface BookingService extends IService<Booking> {
    Booking create(Long userId, BookingDTO dto);
    List<BookingVO> listByUser(Long userId);
    List<BookingVO> listByGuide(Long userId);
    Page<BookingVO> pageAll(Long page, Long size, Integer status);
    void cancel(Long userId, Long id);
    void pay(Long userId, Long id);
    void complete(Long userId, Long id);
}
