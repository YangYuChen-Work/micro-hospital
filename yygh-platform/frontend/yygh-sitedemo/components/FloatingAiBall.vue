<template>
  <div class="ai-ball-wrapper">
    <!-- 展开时的遮罩层 -->
    <div v-if="panelVisible" class="ai-panel-overlay" @click="closePanel"></div>

    <!-- 聊天面板 -->
    <div class="ai-chat-panel" :class="{ 'panel-visible': panelVisible }">
      <div class="panel-header">
        <span class="header-title">
          <i class="el-icon-plus header-cross"></i>
          AI 医疗助手
        </span>
        <div class="header-actions">
          <span class="new-chat-btn" @click="newChat">新会话</span>
          <i class="el-icon-close close-btn" @click="closePanel"></i>
        </div>
      </div>

      <div class="message-list" ref="messageList">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="msg.isUser ? 'msg-user' : 'msg-bot'"
        >
          <div class="msg-content" v-html="msg.content"></div>
          <div v-if="msg.isTyping" class="typing-dots">
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          class="msg-input"
          v-model="inputText"
          placeholder="请输入您的问题..."
          @keyup.enter.native="sendMessage"
        ></el-input>
        <el-button
          type="primary"
          :disabled="isSending"
          @click="sendMessage"
        >发送</el-button>
      </div>
    </div>

    <!-- 悬浮球按钮 -->
    <div
      class="ai-float-ball"
      :class="{ 'ball-active': panelVisible }"
      @click="togglePanel"
    >
      <svg class="ball-robot" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect x="16" y="18" width="32" height="28" rx="6" stroke="currentColor" stroke-width="3" fill="none"/>
        <circle cx="26" cy="32" r="4" fill="currentColor"/>
        <circle cx="38" cy="32" r="4" fill="currentColor"/>
        <path d="M24 42 h16" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
        <rect x="28" y="12" width="8" height="6" rx="3" fill="currentColor"/>
        <circle cx="24" cy="13" r="3" fill="currentColor"/>
        <circle cx="40" cy="13" r="3" fill="currentColor"/>
        <line x1="26" y1="12" x2="24" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <line x1="38" y1="12" x2="40" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
  </div>
</template>

<script>
import { sendChatMessage } from '@/api/xiaozhi'

export default {
  name: 'FloatingAiBall',
  data() {
    return {
      panelVisible: false,
      inputText: '',
      messages: [],
      isSending: false,
      uuid: null
    }
  },
  mounted() {
    this.initUUID()
    this.welcomeMessage()
  },
  methods: {
    togglePanel() {
      this.panelVisible = !this.panelVisible
      if (this.panelVisible) {
        this.$nextTick(() => this.scrollToBottom())
      }
    },
    closePanel() {
      this.panelVisible = false
    },
    initUUID() {
      let stored = localStorage.getItem('ai_assistant_uuid')
      if (!stored) {
        stored = this.generateUUID()
        localStorage.setItem('ai_assistant_uuid', stored)
      }
      this.uuid = stored
    },
    generateUUID() {
      const chars = '0123456789abcdef'
      let uuid = ''
      for (let i = 0; i < 32; i++) {
        uuid += chars[Math.floor(Math.random() * 16)]
      }
      return uuid
    },
    welcomeMessage() {
      this.messages.push({
        isUser: false,
        content: '您好，我是AI医疗助手，可以帮您解答预约挂号、就诊流程、科室选择等问题，请问有什么可以帮助您的？',
        isTyping: false
      })
    },
    sendMessage() {
      const text = this.inputText.trim()
      if (!text || this.isSending) return
      this.inputText = ''
      this.doSend(text)
    },
    doSend(message) {
      this.isSending = true

      this.messages.push({
        isUser: true,
        content: this.escapeHtml(message),
        isTyping: false
      })

      const botMsg = { isUser: false, content: '', isTyping: true }
      this.messages.push(botMsg)
      this.$nextTick(() => this.scrollToBottom())

      sendChatMessage(this.uuid, message, (e) => {
        const fullText = e.event.target.responseText
        const newText = fullText.substring(botMsg.content.length)
        if (newText) {
          botMsg.content += newText
          this.$nextTick(() => this.scrollToBottom())
        }
      })
        .then(() => {
          botMsg.isTyping = false
          this.isSending = false
          botMsg.content = this.formatContent(botMsg.content)
        })
        .catch(() => {
          botMsg.content = '请求失败，请稍后重试'
          botMsg.isTyping = false
          this.isSending = false
        })
    },
    escapeHtml(text) {
      return text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
    },
    formatContent(text) {
      return text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>')
    },
    newChat() {
      this.messages = []
      localStorage.removeItem('ai_assistant_uuid')
      this.initUUID()
      this.welcomeMessage()
    },
    scrollToBottom() {
      const el = this.$refs.messageList
      if (el) {
        el.scrollTop = el.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
/* ========== 悬浮球 ========== */
.ai-float-ball {
  position: fixed;
  bottom: 100px;
  right: 40px;
  z-index: 2000;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid #4990f1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(73, 144, 241, 0.2);
  transition: transform 0.25s, box-shadow 0.25s, background 0.25s;
  animation: breath 2.4s ease-in-out infinite;
}

.ai-float-ball:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(73, 144, 241, 0.35);
}

.ball-robot {
  width: 30px;
  height: 30px;
  color: #4990f1;
  transition: color 0.25s;
}

.ai-float-ball:hover .ball-robot {
  color: #3377dd;
}

.ball-active {
  background: #4990f1;
  animation: none;
  box-shadow: 0 4px 16px rgba(73, 144, 241, 0.3);
}

.ball-active .ball-robot {
  color: #fff;
}

@keyframes breath {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(73, 144, 241, 0.35), 0 4px 16px rgba(73, 144, 241, 0.2);
  }
  50% {
    box-shadow: 0 0 0 12px rgba(73, 144, 241, 0), 0 4px 16px rgba(73, 144, 241, 0.2);
  }
}

/* ========== 遮罩层 ========== */
.ai-panel-overlay {
  position: fixed;
  inset: 0;
  z-index: 1998;
  background: rgba(0, 0, 0, 0.15);
}

/* ========== 聊天面板 ========== */
.ai-chat-panel {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 430px;
  z-index: 1999;
  background: #fff;
  box-shadow: -2px 0 16px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  transform: translateX(100%);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.panel-visible {
  transform: translateX(0);
}

/* 标题栏 */
.panel-header {
  height: 48px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-cross {
  color: #4990f1;
  font-size: 16px;
  font-weight: 700;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.new-chat-btn {
  font-size: 13px;
  color: #4990f1;
  cursor: pointer;
  user-select: none;
}

.new-chat-btn:hover {
  color: #3377dd;
}

.close-btn {
  font-size: 16px;
  color: #999;
  cursor: pointer;
}

.close-btn:hover {
  color: #333;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* AI 消息气泡 */
.msg-bot {
  align-self: flex-start;
  max-width: 85%;
}

.msg-bot .msg-content {
  background: #f8fafc;
  border-left: 3px solid #4990f1;
  border-radius: 4px 8px 8px 4px;
  padding: 12px 16px;
  font-size: 13px;
  color: #333;
  line-height: 1.7;
}

/* 用户消息气泡 */
.msg-user {
  align-self: flex-end;
  max-width: 75%;
}

.msg-user .msg-content {
  background: #e8f2fd;
  border-radius: 8px 4px 4px 8px;
  padding: 10px 14px;
  font-size: 13px;
  color: #333;
  line-height: 1.7;
}

/* 打字动画 */
.typing-dots {
  display: flex;
  gap: 5px;
  padding: 4px 16px;
}

.typing-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4990f1;
  opacity: 0.3;
  animation: dot-blink 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dot-blink {
  0%, 80%, 100% { opacity: 0.2; }
  40% { opacity: 1; }
}

/* 输入区 */
.input-area {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.msg-input {
  flex: 1;
}

.msg-input >>> .el-input__inner {
  border-radius: 20px;
  background: #f5f7fa;
  border-color: #e4e7ed;
  font-size: 13px;
}

.msg-input >>> .el-input__inner:focus {
  border-color: #4990f1;
}

.input-area .el-button--primary {
  border-radius: 20px;
  background: #4990f1;
  border-color: #4990f1;
  padding: 10px 20px;
}

/* ========== 移动端适配 ========== */
@media (max-width: 768px) {
  .ai-float-ball {
    bottom: 80px;
    right: 16px;
    width: 48px;
    height: 48px;
  }

  .ball-robot {
    width: 26px;
    height: 26px;
  }

  .ai-chat-panel {
    width: 100vw;
  }

  .msg-bot {
    max-width: 90%;
  }

  .msg-user {
    max-width: 80%;
  }
}
</style>
