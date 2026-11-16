package com.outdoor.server.controller;

import com.outdoor.server.common.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.entity.Message;
import com.outdoor.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "站内消息", description = "消息列表、未读数量与已读操作")
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public R<List<Message>> list(@RequestAttribute("userId") Long userId) {
        return R.ok(messageService.listByUser(userId));
    }

    @GetMapping("/unread")
    public R<Long> unread(@RequestAttribute("userId") Long userId) {
        return R.ok(messageService.unreadCount(userId));
    }

    @PutMapping("/read/{id}")
    public R<?> read(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        messageService.markRead(userId, id);
        return R.ok();
    }

    @PutMapping("/read-all")
    public R<?> readAll(@RequestAttribute("userId") Long userId) {
        messageService.markAllRead(userId);
        return R.ok();
    }
}
