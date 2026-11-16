package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.server.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    void send(Long fromUserId, Long toUserId, String content);
    List<Message> listByUser(Long userId);
    Long unreadCount(Long userId);
    void markRead(Long userId, Long id);
    void markAllRead(Long userId);
}
