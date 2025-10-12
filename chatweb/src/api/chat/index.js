import request from '@/utils/request'

export function getChatRes(params) {
  return request.post('/gemini/chat', params)
}

export function newChatRes(params) {
  return request.post('/gemini/newChat', params)
}

export function getChatInfoList(chatId) {
  return request.get('/common/chats/' + chatId)
}

export function getChatTitleList(userName) {
  return request.get('/common/titles/' + userName)
}

export function chatWithHistory(params) {
  return request.post('/gemini/historyChat', params)
}

export function deleteChat(chatId) {
  return request.delete('/common/delete/' + chatId)
}

export function saveTitle(params) {
  return request.put('/common/edit', params)
}