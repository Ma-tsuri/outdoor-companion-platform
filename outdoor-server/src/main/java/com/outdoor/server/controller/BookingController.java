package com.outdoor.server.controller;

import com.outdoor.server.common.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.dto.BookingDTO;
import com.outdoor.server.entity.Booking;
import com.outdoor.server.service.BookingService;
import com.outdoor.server.vo.BookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "预约订单", description = "下单、支付、取消、完成与订单查询")
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public R<Booking> create(@RequestAttribute("userId") Long userId, @Valid @RequestBody BookingDTO dto) {
        return R.ok(bookingService.create(userId, dto));
    }

    @GetMapping("/list")
    public R<List<BookingVO>> list(@RequestAttribute("userId") Long userId) {
        return R.ok(bookingService.listByUser(userId));
    }

    @GetMapping("/guide")
    public R<List<BookingVO>> guideList(@RequestAttribute("userId") Long userId) {
        return R.ok(bookingService.listByGuide(userId));
    }

    @PutMapping("/cancel/{id}")
    public R<?> cancel(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        bookingService.cancel(userId, id);
        return R.ok();
    }

    @PutMapping("/pay/{id}")
    public R<?> pay(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        bookingService.pay(userId, id);
        return R.ok();
    }

    @PutMapping("/complete/{id}")
    public R<?> complete(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        bookingService.complete(userId, id);
        return R.ok();
    }
}
