package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author.id", source = "userId")
    @Mapping(target = "item.id", source = "itemId")
    Comment toComment(CommentCreateDto comment, Long userId, Long itemId);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentResponseDto toCommentDto(Comment comment);
}
