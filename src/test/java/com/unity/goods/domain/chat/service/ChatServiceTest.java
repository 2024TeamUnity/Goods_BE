package com.unity.goods.domain.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.unity.goods.domain.chat.dto.ChatRoomDto;
import com.unity.goods.domain.chat.dto.ChatRoomListDto;
import com.unity.goods.domain.chat.entity.ChatLog;
import com.unity.goods.domain.chat.entity.ChatRoom;
import com.unity.goods.domain.chat.repository.ChatLogRepository;
import com.unity.goods.domain.chat.repository.ChatRoomRepository;
import com.unity.goods.domain.goods.entity.Goods;
import com.unity.goods.domain.goods.repository.GoodsRepository;
import com.unity.goods.domain.member.entity.Member;
import com.unity.goods.domain.member.repository.MemberRepository;
import com.unity.goods.global.jwt.UserDetailsImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @InjectMocks
  private ChatService chatService;

  @Mock
  private ChatRoomRepository chatRoomRepository;
  @Mock
  private ChatLogRepository chatLogRepository;
  @Mock
  private GoodsRepository goodsRepository;
  @Mock
  private MemberRepository memberRepository;

  private Member member;
  private Member member2;
  private Goods goods;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(10L)
        .nickname("판매자")
        .build();

    member2 = Member.builder()
        .id(12L)
        .nickname("구매자")
        .build();

    goods = Goods.builder()
        .id(1L)
        .goodsName("테스트제품")
        .member(member)
        .build();
  }

  @Test
  @DisplayName("채팅방 생성")
  void addChatRoom_Test() throws Exception {
    // given
    UserDetailsImpl user = new UserDetailsImpl(member2);
    given(goodsRepository.findById(anyLong())).willReturn(Optional.of(goods));

    // when
    chatService.addChatRoom(goods.getId(), user.getId());

    // then
    verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
  }

  @Test
  @DisplayName("채팅방 목록 조회")
  void getChatRoomList_Test() throws Exception {
    // given
    List<ChatLog> chatLogList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      ChatLog chatLog = ChatLog.builder()
          .senderId(member2.getId())
          .receiverId(member.getId())
          .message("msg" + i)
          .createdAt(LocalDateTime.now().minusMinutes(1))
          .build();
      chatLogList.add(chatLog);
    }

    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .goods(goods)
        .sellerId(member.getId())
        .buyerId(member2.getId())
        .chatLogs(chatLogList)
        .build();

    Pageable pageable = PageRequest.of(0, 10);
    List<ChatRoom> chatRooms = Collections.singletonList(chatRoom);
    Page<ChatRoom> chatRoomsPage = new PageImpl<>(chatRooms, pageable, chatRooms.size());

    given(chatRoomRepository.findAllByBuyerIdOrSellerId(member.getId(), member.getId(),pageable))
        .willReturn(chatRoomsPage);
    given(chatRoomRepository.findOppositeMemberId(chatRoom.getId(), member.getId()))
        .willReturn(member2.getId());
    given(memberRepository.findById(member2.getId()))
        .willReturn(Optional.of(member2));

    // when
    Page<ChatRoomListDto> result = chatService.getChatRoomList(member.getId(), pageable);

    //then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    ChatRoomListDto dto = result.getContent().get(0);
    assertEquals(1L, dto.getRoomId());
    assertEquals("구매자", dto.getPartner());
    assertEquals("msg4", dto.getLastMessage());
    assertNotNull(dto.getUpdatedAt());
    assertEquals(5, dto.getNotRead());
    assertTrue(dto.getUploadedBefore() > 0);
  }

  @Test
  @DisplayName("채팅내역 조회")
  void getChatLog_Test() throws Exception{
    // given
    List<ChatLog> chatLogList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      ChatLog chatLog = ChatLog.builder()
          .senderId(member2.getId())
          .receiverId(member.getId())
          .message("msg" + i)
          .createdAt(LocalDateTime.now())
          .build();

      chatLogList.add(chatLog);
    }

    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .goods(goods)
        .sellerId(member.getId())
        .buyerId(member2.getId())
        .chatLogs(chatLogList)
        .build();

    Pageable pageable = PageRequest.of(0, 5);
    Page<ChatLog> chatLogPage = new PageImpl<>(chatLogList, pageable, chatLogList.size());

    given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));
    given(chatRoomRepository.findOppositeMemberId(chatRoom.getId(), member.getId())).willReturn(member2.getId());
    given(memberRepository.findById(member2.getId())).willReturn(Optional.of(member2));
    given(chatLogRepository.findByChatRoomId(chatRoom.getId(), pageable)).willReturn(chatLogPage);

    // when
    ChatRoomDto result = chatService.getChatLogs(chatRoom.getId(), member.getId(), pageable);

    // then
    assertNotNull(result);
    assertEquals(1L, result.getRoomId());
    assertEquals(1L, result.getGoodsId());
    assertEquals(10L, result.getMemberId());
    assertEquals("구매자", result.getPartner());
    assertEquals("테스트제품", result.getGoodsName());
    assertEquals(5, result.getChatLogs().getTotalElements());
    assertEquals(1, result.getChatLogs().getTotalPages());
    assertEquals(0, result.getChatLogs().getNumber());
  }

  @Test
  @DisplayName("채팅방 나가기")
  public void testLeaveChatRoom_SellerLeft() {
    // given
    ChatRoom chatRoom = ChatRoom.builder().id(1L)
        .sellerId(member.getId())
        .buyerId(member2.getId()).build();

    chatRoom.setSellerLeft(true);
    chatRoom.setBuyerLeft(false);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

    // when
    chatService.leaveChatRoom(1L,member.getId());

    // then
    assertTrue(chatRoom.isSellerLeft());
    assertFalse(chatRoom.isBuyerLeft());
    verify(chatRoomRepository).save(chatRoom);

    // given
    chatRoom.setSellerLeft(false);
    chatRoom.setBuyerLeft(true);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

    // when
    chatService.leaveChatRoom(1L,member2.getId());

    // then
    assertFalse(chatRoom.isSellerLeft());
    assertTrue(chatRoom.isBuyerLeft());
    verify(chatRoomRepository,times(2)).save(chatRoom);
  }

  @Test
  @DisplayName("채팅방 메세지 전송 - 채팅방 다시초대")
  void testInviteRoom() throws Exception{
    // given
    ChatRoom chatRoom = ChatRoom.builder().id(1L)
        .sellerId(member.getId())
        .buyerId(member2.getId()).build();

    chatRoom.setSellerLeft(false);
    chatRoom.setBuyerLeft(true);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

    // when
    chatService.inviteChatRoom(1L);

    //then
    assertFalse(chatRoom.isSellerLeft());
    assertFalse(chatRoom.isBuyerLeft());
    verify(chatRoomRepository).save(chatRoom);

    // given
    chatRoom.setSellerLeft(true);
    chatRoom.setBuyerLeft(false);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

    // when
    chatService.inviteChatRoom(1L);

    //then
    assertFalse(chatRoom.isSellerLeft());
    assertFalse(chatRoom.isBuyerLeft());
    verify(chatRoomRepository, times(2)).save(chatRoom);
  }
}