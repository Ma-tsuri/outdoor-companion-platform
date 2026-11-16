package com.outdoor.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.server.entity.Message;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.MessageMapper;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void send(Long fromUserId, Long toUserId, String content) {
        if (toUserId == null || content == null || content.trim().isEmpty()) {
            return;
        }
        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setIsRead(0);
        save(message);
    }

    @Override
    public List<Message> listByUser(Long userId) {
        List<Message> list = lambdaQuery().eq(Message::getToUserId, userId)
                .orderByDesc(Message::getCreateTime).list();
        if (list.isEmpty()) {
            return list;
        }
        Set<Long> userIds = list.stream().map(Message::getFromUserId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (Message message : list) {
            User user = userMap.get(message.getFromUserId());
            message.setFromUserName(user == null ? "系统通知" : user.getUsername());
        }
        return list;
    }

    @Override
    public Long unreadCount(Long userId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0));
    }

    @Override
    public void markRead(Long userId, Long id) {
        update(new LambdaUpdateWrapper<Message>()
                .set(Message::getIsRead, 1)
                .eq(Message::getId, id)
                .eq(Message::getToUserId, userId));
    }

    @Override
    public void markAllRead(Long userId) {
        update(new LambdaUpdateWrapper<Message>()
                .set(Message::getIsRead, 1)
                .eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0));
    }
}
