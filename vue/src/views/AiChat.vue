<template>
  <div class="chat-container">
    <!-- 装饰性背景 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <div class="chat-header">
      <div class="header-content">
        <div class="header-icon">
          <i class="el-icon-s-help"></i>
        </div>
        <div class="header-text">
          <h2>枣专家 - AI农业助手</h2>
          <p>专业的酸枣全产业链智能咨询服务</p>
        </div>
      </div>
      <div class="header-actions">
        <el-tooltip content="刷新对话" placement="bottom">
          <i class="el-icon-refresh refresh-btn"></i>
        </el-tooltip>
      </div>
    </div>
    
    <div class="chat-main">
      <div class="chat-messages" ref="messageContainer">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.isUser ? 'user-message' : 'ai-message']">
          <div class="message-content">
            <div class="message-avatar">
              <div class="avatar-bg" :class="{ 'user-avatar': msg.isUser, 'ai-avatar': !msg.isUser }">
                <i :class="msg.isUser ? 'el-icon-user' : 'el-icon-s-help'"></i>
              </div>
              <div v-if="!msg.isUser" class="ai-pulse"></div>
            </div>
            <div class="message-text" v-html="msg.content"></div>
          </div>
          <div class="message-time">{{ formatTime(msg.time) }}</div>
        </div>
        <div v-if="loading" class="message ai-message">
          <div class="message-content">
            <div class="message-avatar">
              <div class="avatar-bg ai-avatar">
                <i class="el-icon-s-help"></i>
              </div>
              <div class="ai-pulse"></div>
            </div>
            <div class="message-text">
              <div class="loading-content">
                <div class="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <span>AI正在思考中...</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <div class="input-container">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题，例如：酸枣常见病虫害有哪些？如何防治？"
            v-model="inputMessage"
            @keyup.enter.native="sendMessage"
            class="message-input"
          ></el-input>
          <div class="input-tools">
            <el-upload
              class="upload-btn"
              :show-file-list="false"
              :before-upload="beforeUpload"
              action=""
              accept=".jpg,.jpeg,.png"
            >
              <el-button type="text" icon="el-icon-picture" class="tool-btn" title="上传图片进行病虫害检测">图片</el-button>
            </el-upload>
          </div>
        </div>
        <div class="input-actions">
          <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()" class="send-btn">
            <i class="el-icon-s-promotion"></i>
            发送
          </el-button>
        </div>
      </div>
    </div>
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <div class="header-content">
          <div class="header-icon">
            <i class="el-icon-time"></i>
          </div>
          <h3>历史记录</h3>
        </div>
        <el-button type="text" @click="clearHistory" v-if="chatHistory.length > 0" class="clear-btn">
          <i class="el-icon-delete"></i> 清空
        </el-button>
      </div>
      <div class="history-list">
        <div class="quick-questions">
          <div class="section-header">
            <i class="el-icon-star-on"></i>
            <h4>常见问题</h4>
          </div>
          <div class="question-tags">
            <el-tag 
              v-for="(question, index) in quickQuestions" 
              :key="index" 
              type="info" 
              @click="handleQuickQuestion(question)"
              class="question-tag"
            >
              {{ question }}
            </el-tag>
          </div>
        </div>
        <div class="history-divider"></div>
        <div class="history-section">
          <div class="section-header">
            <i class="el-icon-chat-dot-round"></i>
            <h4>对话历史</h4>
          </div>
          <div v-for="(chat, index) in chatHistory" :key="index" 
               class="history-item" 
               :class="{ active: currentChatId === chat.id }"
               @click="loadChat(chat.id)">
            <div class="history-content">
              <div class="history-title">{{ chat.question.substring(0, 25) }}{{ chat.question.length > 25 ? '...' : '' }}</div>
              <div class="history-time">{{ formatTime(chat.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import DOMPurify from 'dompurify'
import request from '@/utils/request'

export default {
  name: 'AiChat',
  data() {
    return {
      messages: [],
      inputMessage: '',
      loading: false,
      chatHistory: [],
      currentChatId: null,
      user: JSON.parse(localStorage.getItem('user')) || {},
      quickQuestions: [
        '酸枣常见病虫害有哪些？',
        '如何提高酸枣产量？',
        '酸枣最佳种植时间是什么时候？',
        '酸枣种植需要什么土壤条件？',
        '如何防治酸枣白粉病？'
      ]
    }
  },
  created() {
    this.loadHistory()
  },
  methods: {
    async sendMessage() {
      if (!this.inputMessage.trim()) return
      
      const question = this.inputMessage.trim()
      this.inputMessage = ''
      
      // 添加用户消息
      this.messages.push({
        content: DOMPurify.sanitize(question),
        isUser: true,
        time: new Date()
      })
      
      this.loading = true
      this.scrollToBottom()
      
      try {
        const res = await request.post('/api/ai/chat', {
          message: question,
          userId: this.user.id || 'anonymous',
          username: this.user.username || '用户'
        })
        console.log('AI聊天响应:', res)
        if (res && res.answer) {
          // 添加AI回复
          this.messages.push({
            content: DOMPurify.sanitize(this.formatAnswer(res.answer)),
            isUser: false,
            time: new Date()
          })
          // 更新历史记录
          this.loadHistory()
        } else {
          console.error('AI响应格式错误:', res)
          this.$message.error('获取回答失败')
        }
      } catch (error) {
        console.error('AI聊天请求失败:', error)
        this.$message.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },
    
    // 处理快速问题点击
    handleQuickQuestion(question) {
      this.inputMessage = question
      this.sendMessage()
    },
    
    // 格式化AI回答，添加换行和段落
    formatAnswer(answer) {
      if (!answer) return ''
      // 将换行符转换为<br>，并添加适当的段落分隔
      return answer
        .replace(/\n{2,}/g, '</p><p>')
        .replace(/\n/g, '<br>')
        .replace(/^(.*?)$/, '<p>$1</p>')
    },
    
    // 图片上传前的处理
    beforeUpload(file) {
      this.uploadPestImage(file)
      return false // 阻止默认上传行为
    },
    
    // 上传病虫害图片进行检测
    async uploadPestImage(file) {
      const formData = new FormData()
      formData.append('file', file)
      if (this.user.id) {
        formData.append('userId', this.user.id)
      }
      
      // 添加用户消息显示上传的图片
      this.messages.push({
        content: `<div class="image-message"><img src="${URL.createObjectURL(file)}" alt="上传的图片"></div>`,
        isUser: true,
        time: new Date()
      })
      
      this.loading = true
      this.scrollToBottom()
      
      try {
        const res = await request.post('/api/ai/detect', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (res.data) {
          let resultContent = ''
          if (res.data.result) {
            // 如果返回了完整的检测结果
            resultContent = DOMPurify.sanitize(this.formatAnswer(res.data.result))
          } else {
            // 否则根据返回的字段构建结果
            resultContent = `<p><strong>检测结果：</strong></p>
                          <p><strong>病虫害名称：</strong>${res.data.pestName}</p>
                          <p><strong>置信度：</strong>${(res.data.confidence * 100).toFixed(2)}%</p>
                          <p><strong>建议：</strong>${res.data.result || '请参考相关农业知识进行防治'}</p>`
          }
          
          // 添加AI回复
          this.messages.push({
            content: resultContent,
            isUser: false,
            time: new Date()
          })
        } else {
          this.$message.error('检测失败，未获取到结果')
        }
      } catch (error) {
        this.$message.error('病虫害检测失败，请稍后重试')
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },
    
    async loadHistory() {
      try {
        const res = await request.get('/api/ai/chat/history', {
          params: { username: this.user.username }
        })
        if (res.data) {
          this.chatHistory = res.data
        }
      } catch (error) {
        this.$message.error('加载历史记录失败')
      }
    },
    
    async loadChat(id) {
      this.currentChatId = id
      try {
        const res = await request.get(`/api/ai/chat/${id}`, {
          params: { username: this.user.username }
        })
        if (res.data) {
          this.messages = [
            {
              content: DOMPurify.sanitize(res.data.question),
              isUser: true,
              time: new Date(res.data.createTime)
            },
            {
              content: DOMPurify.sanitize(this.formatAnswer(res.data.answer)),
              isUser: false,
              time: new Date(res.data.createTime)
            }
          ]
        }
      } catch (error) {
        this.$message.error('加载聊天记录失败')
      }
    },
    
    async clearHistory() {
      try {
        await this.$confirm('确定要清空所有历史记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await request.delete('/api/ai/chat/history', {
          params: { username: this.user.username }
        })
        if (res.status === 204) {
          this.chatHistory = []
          this.messages = []
          this.currentChatId = null
          this.$message.success('历史记录已清空')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('清空历史记录失败')
        }
      }
    },
    
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        container.scrollTop = container.scrollHeight
      })
    }
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  overflow: hidden;
  position: relative;
}

/* 装饰性背景 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 30%;
  right: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.chat-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 20px rgba(0,0,0,0.1);
  position: relative;
  z-index: 1;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.header-text h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: 600;
}

.header-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.refresh-btn {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
}

.refresh-btn:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
  transform: rotate(180deg);
}

.chat-main {
  flex: 1;
  display: flex;
  background: rgba(255, 255, 255, 0.95);
  margin: 10px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%);
}

.message {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  animation: slideIn 0.4s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.message-content {
  display: flex;
  align-items: flex-start;
  max-width: 80%;
  gap: 12px;
}

.user-message {
  align-items: flex-end;
}

.user-message .message-content {
  flex-direction: row-reverse;
}

.message-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-bg {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.user-avatar {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.ai-pulse {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.3;
  animation: aiPulse 2s infinite;
}

@keyframes aiPulse {
  0% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.1; }
  100% { transform: scale(1); opacity: 0.3; }
}

/* 图片消息样式 */
.image-message {
  max-width: 300px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-message img {
  width: 100%;
  height: auto;
  display: block;
}

/* 格式化的AI回答样式 */
.message-text {
  background: rgba(255, 255, 255, 0.9);
  padding: 15px 20px;
  border-radius: 18px;
  word-break: break-word;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.user-message .message-text {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: white;
  border: none;
}

.message-text p {
  margin: 10px 0;
  line-height: 1.6;
}

.message-text strong {
  color: #667eea;
  font-weight: 600;
}

.user-message .message-text strong {
  color: rgba(255, 255, 255, 0.9);
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  animation: loadingDots 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes loadingDots {
  0%, 80%, 100% { 
    transform: scale(0);
    opacity: 0.5;
  } 
  40% { 
    transform: scale(1);
    opacity: 1;
  }
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  margin-left: 57px;
  font-weight: 500;
}

.user-message .message-time {
  margin-left: 0;
  margin-right: 57px;
  text-align: right;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid rgba(235, 238, 245, 0.5);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

.input-container {
  position: relative;
  margin-bottom: 15px;
}

.message-input {
  width: 100%;
}

.message-input :deep(.el-textarea__inner) {
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 15px;
  padding: 15px 50px 15px 15px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  font-size: 14px;
  line-height: 1.5;
}

.message-input :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.input-tools {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  gap: 5px;
}

.tool-btn {
  padding: 8px;
  border-radius: 8px;
  color: #909399;
  transition: all 0.3s ease;
}

.tool-btn:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.send-btn {
  min-width: 100px;
  height: 40px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.send-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  background: #c0c4cc;
  box-shadow: none;
  transform: none;
}

.chat-sidebar {
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  margin: 10px 10px 10px 0;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(235, 238, 245, 0.5);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%);
  border-radius: 20px 20px 0 0;
}

.sidebar-header .header-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar-header .header-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.clear-btn {
  color: #909399;
  transition: color 0.3s ease;
}

.clear-btn:hover {
  color: #F56C6C;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.quick-questions {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding: 0 5px;
}

.section-header i {
  color: #667eea;
  font-size: 16px;
}

.section-header h4 {
  margin: 0;
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.question-tags {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.question-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 15px;
  padding: 8px 12px;
  font-size: 13px;
  line-height: 1.4;
  text-align: left;
  border: 1px solid rgba(102, 126, 234, 0.2);
  background: rgba(102, 126, 234, 0.05);
  color: #667eea;
}

.question-tag:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateX(5px);
}

.history-divider {
  height: 1px;
  background: rgba(235, 238, 245, 0.5);
  margin: 20px 0;
}

.history-section {
  margin-top: 20px;
}

.history-item {
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.5);
}

.history-item:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(5px);
}

.history-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

.history-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.history-title {
  font-size: 13px;
  color: #303133;
  line-height: 1.4;
  font-weight: 500;
}

.history-time {
  font-size: 11px;
  color: #909399;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    height: calc(100vh - 80px);
    margin: 10px;
  }
  
  .chat-main {
    margin: 5px;
  }
  
  .chat-sidebar {
    width: 100%;
    margin: 5px 0;
    order: -1;
    max-height: 200px;
  }
  
  .chat-header {
    padding: 15px;
  }
  
  .header-text h2 {
    font-size: 20px;
  }
  
  .header-text p {
    font-size: 12px;
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .question-tags {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .question-tag {
    flex: 1;
    min-width: 120px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .chat-container {
    margin: 5px;
    border-radius: 15px;
  }
  
  .chat-header {
    padding: 10px;
  }
  
  .header-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
  
  .header-text h2 {
    font-size: 18px;
  }
  
  .chat-messages {
    padding: 15px;
  }
  
  .chat-input {
    padding: 15px;
  }
  
  .message-text {
    padding: 12px 16px;
    font-size: 13px;
  }
}
</style>