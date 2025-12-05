<template>
  <div class="chat-container">
    <!-- 顶部装饰性背景 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <el-row :gutter="20">
      <!-- 左侧在线用户列表 -->
      <el-col :span="6">
        <el-card class="user-list-card" shadow="hover">
          <div class="card-header">
            <div class="header-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="header-content">
              <span class="header-title">在线专家</span>
              <span class="header-subtitle">{{ users.length }} 位专家在线</span>
            </div>
            <el-tooltip content="点击专家头像开始咨询" placement="right">
              <i class="el-icon-question help-icon"></i>
            </el-tooltip>
          </div>

          <!-- 用户列表渲染 -->
          <div class="user-list">
            <template v-if="users.length > 0">
              <div
                  v-for="user in users"
                  :key="user.username"
                  class="user-item"
                  :class="{ active: user.username === chatUser }"
                  @click="selectUser(user)"
              >
                <div class="user-avatar">
                  <img :src="user.avatar || circleUrl" :alt="user.username" class="avatar-img">
                  <span class="status-dot" :class="{ online: user.online }"></span>
                </div>
                <div class="user-info">
                  <span class="username">{{ user.username }}</span>
                  <span class="user-title">农业专家</span>
                </div>
                <div class="user-actions">
                  <div class="action-btn" :class="{ active: user.username === chatUser }">
                    <i class="el-icon-chat-dot-round chat-icon"></i>
                  </div>
                  <span v-if="user.username === chatUser" class="chatting-tag">咨询中...</span>
                </div>
              </div>
            </template>

            <!-- 空状态提示 -->
            <div v-else class="empty-state">
              <div class="empty-icon">
                <i class="el-icon-user"></i>
              </div>
              <p>暂无其他专家在线</p>
              <p class="empty-tip">专家上线后将显示在这里</p>
            </div>
          </div>

          <!-- AI助手入口 -->
          <div class="ai-assistant">
            <div class="ai-header" @click="selectAI">
              <div class="ai-avatar">
                <div class="ai-avatar-bg">
                  <i class="el-icon-s-help"></i>
                </div>
                <span class="ai-pulse"></span>
              </div>
              <div class="ai-info">
                <span class="ai-title">AI智能助手</span>
                <span class="ai-subtitle">24小时在线服务</span>
              </div>
              <div class="ai-actions">
                <div class="action-btn" :class="{ active: chatUser === 'AI助手' }">
                  <i class="el-icon-s-help ai-icon"></i>
                </div>
                <span v-if="chatUser === 'AI助手'" class="chatting-tag ai-tag">咨询中...</span>
              </div>
            </div>

            <!-- AI助手历史记录 -->
            <div v-if="chatUser === 'AI助手'" class="ai-history">
              <div class="history-header">
                <span class="history-title">
                  <i class="el-icon-time"></i>
                  历史记录
                </span>
                <el-button 
                  v-if="aiHistory.length > 0"
                  type="text" 
                  class="clear-btn"
                  @click="clearAIHistory"
                >
                  <i class="el-icon-delete"></i> 清空
                </el-button>
              </div>
              <div class="history-list">
                <div v-for="(chat, index) in aiHistory" 
                     :key="index" 
                     class="history-item" 
                     :class="{ active: currentChatId === chat.id }"
                     @click="loadAIChat(chat.id)">
                  <div class="history-content">
                    <div class="history-title">{{ chat.question.substring(0, 20) }}{{ chat.question.length > 20 ? '...' : '' }}</div>
                    <div class="history-time">{{ formatTime(chat.createTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧聊天主区域 -->
      <el-col :span="18">
        <el-card class="chat-card" shadow="hover">
          <div class="chat-header">
            <div class="chat-header-content">
              <div class="chat-avatar">
                <img v-if="chatUser && chatUser !== 'AI助手'" :src="circleUrl" :alt="chatUser" class="header-avatar">
                <div v-else class="ai-header-avatar">
                  <i class="el-icon-s-help"></i>
                </div>
              </div>
              <div class="chat-info">
                <h3 class="chat-title">
                  {{ chatUser === 'AI助手' ? 'AI智能助手' : `专家咨询 - ${chatUser || "未选择专家"}` }}
                </h3>
                <p class="chat-status">
                  {{ chatUser === 'AI助手' ? '24小时在线服务' : (chatUser ? '在线' : '请选择专家开始咨询') }}
                </p>
              </div>
            </div>
            <div class="chat-actions">
              <el-tooltip content="刷新" placement="bottom">
                <i class="el-icon-refresh refresh-btn"></i>
              </el-tooltip>
            </div>
          </div>

          <!-- 消息显示区域 -->
          <div class="message-container" ref="messageContainer">
            <div v-if="!chatUser" class="welcome-message">
              <div class="welcome-content">
                <img :src="welcomeLogoUrl" alt="Logo" class="welcome-logo">
                <h2>欢迎使用枣往果来--"枣专家"系统</h2>
                <p>专业的酸枣种植咨询平台</p>
                <div class="welcome-features">
                  <div class="feature-item">
                    <i class="el-icon-s-help"></i>
                    <span>AI智能助手</span>
                  </div>
                  <div class="feature-item">
                    <i class="el-icon-user"></i>
                    <span>专家在线咨询</span>
                  </div>
                  <div class="feature-item">
                    <i class="el-icon-picture"></i>
                    <span>病虫害识别</span>
                  </div>
                </div>
                <p class="welcome-tip">请选择左侧专家或AI助手开始咨询</p>
              </div>
            </div>
            <div v-else v-html="content" class="message-list"></div>
            <div v-if="aiLoading" class="loading-message">
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

          <!-- 输入区域 -->
          <div class="input-area">
            <div class="input-wrapper">
              <div class="input-container">
                <textarea
                    v-model="text"
                    class="message-input"
                    :placeholder="chatUser === 'AI助手' ? '请输入关于酸枣种植的问题，例如：如何防治酸枣病虫害？' : '输入消息，按回车发送'"
                    @keyup.enter="send"
                    rows="3"
                ></textarea>
                <div class="input-tools">
                  <el-upload
                      v-if="chatUser !== 'AI助手'"
                      action="/api/upload"
                      :show-file-list="false"
                      :on-success="handleFileSuccess"
                      class="upload-btn"
                  >
                    <el-button type="text" size="small" icon="el-icon-paperclip" class="tool-btn">附件</el-button>
                  </el-upload>
                  <el-button 
                      v-if="chatUser === 'AI助手'"
                      type="text" 
                      size="small" 
                      icon="el-icon-picture" 
                      class="tool-btn"
                      @click="$refs.imageUpload.click()"
                  >
                    图片
                  </el-button>
                  <input 
                      ref="imageUpload" 
                      type="file" 
                      accept="image/*" 
                      style="display: none" 
                      @change="handleImageUpload"
                  >
                </div>
              </div>
              <div class="input-actions">
                <el-button 
                    type="primary" 
                    :disabled="!text.trim()"
                    @click="send"
                    class="send-btn"
                >
                  <i class="el-icon-s-promotion"></i>
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from "@/utils/request";
import DOMPurify from "dompurify";
import logoUrl from '@/assets/logo.png'; // 引入 Logo 图片

let socket;
let typingTimer = null;

export default {
  name: "Im",
  data() {
    return {
      circleUrl: require('@/assets/login1.png'),
      user: {},
      users: [],
      chatUser: "",
      text: "",
      messages: [],
      content: "",
      aiLoading: false,
      aiHistory: [],
      currentChatId: null,
      isTyping: false,
      typingText: '',
      currentTypingIndex: 0,
      typingSpeed: 30, // 打字速度（毫秒/字符）
      welcomeLogoUrl: logoUrl, // 存储 Logo 图片 URL
    };
  },
  created() {
    this.user = localStorage.getItem("user")
        ? JSON.parse(localStorage.getItem("user"))
        : {};
    this.init();
  },
  watch: {
    chatUser(newVal) {
      if (newVal === 'AI助手') {
        this.loadAIHistory();
      } else if (newVal) {
        this.loadHistory();
      }
      this.scrollToBottom();
    }
  },
  methods: {
    // 选择用户
    selectUser(user) {
      this.chatUser = user.username;
      this.content = "";
    },

    // 选择AI助手
    async selectAI() {
      this.chatUser = "AI助手";
      this.content = "";
      this.loadAIHistory();
    },

    // 加载历史消息
    loadHistory() {
      if (this.chatUser === "AI助手") return;
      request.get("/api/messages", {
        params: { from: this.user.username, to: this.chatUser }
      }).then(res => {
        res.data.forEach(msg => {
          msg.from === this.user.username
              ? this.createContent(null, this.user.username, msg.text)
              : this.createContent(msg.from, null, msg.text);
        });
      });
    },

    // 发送消息
    async send() {
      if (!this.chatUser) {
        this.$message({ type: "warning", message: "请选择聊天对象" });
        return;
      }

      if (this.chatUser === "AI助手") {
        await this.handleAIChat();
      } else {
        if (!this.text.trim()) {
          this.$message({ type: "warning", message: "请输入内容" });
          return;
        }

        const sanitizedText = DOMPurify.sanitize(this.text);
        const message = {
          from: this.user.username,
          to: this.chatUser,
          text: sanitizedText,
          status: "sent"
        };

        if (typeof WebSocket !== "undefined") {
          socket.send(JSON.stringify(message));
          this.messages.push(message);
          this.createContent(null, this.user.username, sanitizedText);
          this.text = "";
        }
      }
    },

    // 处理AI对话
    async handleAIChat() {
      if (!this.text.trim()) return;

      const userQuestion = DOMPurify.sanitize(this.text);
      this.createContent(null, this.user.username, userQuestion);

      try {
        this.aiLoading = true;
        this.text = "";

        const res = await request.post("/api/ai/chat", {
          question: userQuestion,
          username: this.user.username
        });

        if (res.data && res.data.answer) {
          const aiAnswer = DOMPurify.sanitize(res.data.answer);
          // 使用打字机效果显示AI回答
          this.startTypingEffect(aiAnswer);
          this.loadAIHistory(); // 刷新历史记录
        } else {
          this.$message.error('获取回答失败');
        }
      } catch (err) {
        this.createContent("AI助手", null, "服务暂时不可用，请稍后再试");
      } finally {
        this.aiLoading = false;
      }
    },

    // 打字机效果
    startTypingEffect(text) {
      this.isTyping = true;
      this.typingText = '';
      this.currentTypingIndex = 0;
      
      const typeNextChar = () => {
        if (this.currentTypingIndex < text.length) {
          this.typingText += text[this.currentTypingIndex];
          this.currentTypingIndex++;
          
          // 更新消息显示
          this.updateTypingMessage();
          
          // 继续打字
          typingTimer = setTimeout(typeNextChar, this.typingSpeed);
        } else {
          // 打字结束
          this.isTyping = false;
          this.createContent("AI助手", null, text);
          this.typingText = '';
          this.scrollToBottom();
        }
      };

      // 开始打字
      typeNextChar();
    },

    // 更新正在输入的消息
    updateTypingMessage() {
      const tempContent = this.content;
      const lastMessageIndex = tempContent.lastIndexOf('<div class="el-row"');
      
      if (lastMessageIndex !== -1) {
        // 移除最后一条消息
        this.content = tempContent.substring(0, lastMessageIndex);
      }
      
      // 添加新的打字消息
      const typingHtml = `
        <div class="el-row" style="padding: 5px 0">
          <div class="el-col el-col-2">
            <img :src="require('@/assets/logo1.png')"
                 style="height:40px;width:40px;border-radius:50%">
          </div>
          <div class="el-col el-col-22" style="text-align: left; padding-left: 10px">
            <div class="tip right typing-message" style="background-color: #a0d8ef">
              ${this.typingText}
              <span class="typing-cursor">|</span>
            </div>
            <span style="font-size: 12px; color: #999">
              ${new Date().toLocaleTimeString()}
            </span>
          </div>
        </div>`;
      
      this.content += typingHtml;
      this.scrollToBottom();
    },

    // 组件销毁时清理定时器
    beforeDestroy() {
      if (typingTimer) {
        clearTimeout(typingTimer);
      }
    },

    // 构建消息内容
    createContent(remoteUser, nowUser, text) {
      let html;
      const sanitizedText = DOMPurify.sanitize(text);

      if (nowUser) {
        html = `
          <div class="el-row" style="padding: 5px 0">
            <div class="el-col el-col-22" style="text-align: right; padding-right: 10px">
              <div class="tip left">${sanitizedText}</div>
              <span style="font-size: 12px; color: #999">
                ${new Date().toLocaleTimeString()}
                ${nowUser === this.user.username ? '✔️' : ''}
              </span>
            </div>
            <div class="el-col el-col-2">
              <img src="${this.circleUrl}" style="height:40px;width:40px;border-radius:50%">
            </div>
          </div>`;
      } else if (remoteUser === "AI助手") {
        html = `
          <div class="el-row" style="padding: 5px 0">
            <div class="el-col el-col-2">
              <img :src="require('@/assets/logo1.png')"
                   style="height:40px;width:40px;border-radius:50%">
            </div>
            <div class="el-col el-col-22" style="text-align: left; padding-left: 10px">
              <div class="tip right" style="background-color: #a0d8ef">${sanitizedText}</div>
              <span style="font-size: 12px; color: #999">
                ${new Date().toLocaleTimeString()}
              </span>
            </div>
          </div>`;
      } else {
        html = `
          <div class="el-col el-col-2">
            <img src="${this.circleUrl}" style="height:40px;width:40px;border-radius:50%">
          </div>
          <div class="el-col el-col-22" style="text-align: left; padding-left: 10px">
            <div class="tip right">${sanitizedText}</div>
            <span style="font-size: 12px; color: #999">
              ${new Date().toLocaleTimeString()}
            </span>
          </div>`;
      }

      this.content += html;
      this.scrollToBottom();
    },

    // 文件上传处理
    handleFileSuccess(res) {
      const message = {
        from: this.user.username,
        to: this.chatUser,
        text: `[文件] ${res.fileName}`,
        fileUrl: res.url
      };
      socket.send(JSON.stringify(message));
      this.createContent(null, this.user.username, message.text);
    },

    // 处理图片上传
    handleImageUpload(event) {
      const file = event.target.files[0];
      if (file && this.chatUser === 'AI助手') {
        this.uploadPestImage(file);
      }
      // 清空input值，允许重复选择同一文件
      event.target.value = '';
    },

    // 上传病虫害图片进行检测
    async uploadPestImage(file) {
      const formData = new FormData();
      formData.append('file', file);
      if (this.user.id) {
        formData.append('userId', this.user.id);
      }
      
      // 添加用户消息显示上传的图片
      this.createContent(null, this.user.username, `<div class="image-message"><img src="${URL.createObjectURL(file)}" alt="上传的图片"></div>`);
      
      this.aiLoading = true;
      this.scrollToBottom();
      
      try {
        const res = await request.post('/api/ai/detect', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        if (res.data) {
          let resultContent = '';
          if (res.data.result) {
            resultContent = DOMPurify.sanitize(res.data.result);
          } else {
            resultContent = `<p><strong>检测结果：</strong></p>
                          <p><strong>病虫害名称：</strong>${res.data.pestName}</p>
                          <p><strong>置信度：</strong>${(res.data.confidence * 100).toFixed(2)}%</p>
                          <p><strong>建议：</strong>${res.data.result || '请参考相关农业知识进行防治'}</p>`;
          }
          
          this.createContent("AI助手", null, resultContent);
        } else {
          this.$message.error('检测失败，未获取到结果');
        }
      } catch (error) {
        this.createContent("AI助手", null, "病虫害检测失败，请稍后重试");
      } finally {
        this.aiLoading = false;
      }
    },

    // 自动滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer;
        if (container) container.scrollTop = container.scrollHeight;
      });
    },

    // 初始化WebSocket
    init() {
      const username = this.user.username;
      if (typeof WebSocket === "undefined") return;

      const connect = () => {
        if (socket) socket.close();
        socket = new WebSocket(`ws://localhost:8080/api/imserver/${username}`);

        socket.onmessage = (msg) => {
          const data = JSON.parse(msg.data);
          if (data.type === "users") {
            this.users = data.users.filter(u => u.username !== username);
          } else if (data.from === this.chatUser) {
            this.createContent(data.from, null, data.text);
          }
        };

        socket.onclose = () => setTimeout(connect, 5000);
      };

      connect();
    },

    // AI助手相关方法
    async loadAIHistory() {
      try {
        const res = await request.get("/api/ai/chat/history", {
          params: { username: this.user.username }
        });
        if (res.data) {
          this.aiHistory = res.data;
        }
      } catch (error) {
        this.$message.error('加载历史记录失败');
      }
    },

    async loadAIChat(id) {
      this.currentChatId = id;
      try {
        const res = await request.get(`/api/ai/chat/${id}`, {
          params: { username: this.user.username }
        });
        if (res.data) {
          this.content = '';
          this.createContent(null, this.user.username, res.data.question);
          this.createContent("AI助手", null, res.data.answer);
        }
      } catch (error) {
        this.$message.error('加载聊天记录失败');
      }
    },

    async clearAIHistory() {
      try {
        await this.$confirm('确定要清空所有历史记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        const res = await request.delete("/api/ai/chat/history", {
          params: { username: this.user.username }
        });
        if (res.status === 204) {
          this.aiHistory = [];
          this.content = '';
          this.currentChatId = null;
          this.$message.success('历史记录已清空');
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('清空历史记录失败');
        }
      }
    },

    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.chat-container {
  padding: 20px;
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
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

/* 卡片样式 */
.user-list-card, .chat-card {
  height: calc(100vh - 140px);
  border-radius: 20px;
  border: none;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.user-list-card:hover, .chat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* 卡片头部 */
.card-header {
  padding: 20px;
  border-bottom: 1px solid rgba(235, 238, 245, 0.5);
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%);
  border-radius: 20px 20px 0 0;
}

.header-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.header-content {
  flex: 1;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: block;
  margin-bottom: 2px;
}

.header-subtitle {
  font-size: 12px;
  color: #909399;
  display: block;
}

.help-icon {
  color: #909399;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.help-icon:hover {
  color: #409EFF;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid rgba(235, 238, 245, 0.5);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px 20px 0 0;
}

.chat-header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chat-avatar {
  position: relative;
}

.header-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.ai-header-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.chat-info {
  flex: 1;
}

.chat-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: white;
}

.chat-status {
  font-size: 14px;
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
}

.chat-actions {
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

/* 用户列表样式 */
.user-list {
  padding: 15px;
  max-height: calc(100% - 200px);
  overflow-y: auto;
}

.user-item {
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.5);
}

.user-item:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(5px);
}

.user-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

.user-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #909399;
  border: 2px solid white;
}

.status-dot.online {
  background-color: #67C23A;
  box-shadow: 0 0 0 2px rgba(103, 194, 58, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(103, 194, 58, 0); }
  100% { box-shadow: 0 0 0 0 rgba(103, 194, 58, 0); }
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.user-title {
  font-size: 12px;
  color: #909399;
}

.user-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.action-btn {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.action-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: scale(1.1);
}

.action-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.chat-icon {
  font-size: 16px;
  color: #667eea;
  transition: all 0.3s ease;
}

.action-btn.active .chat-icon {
  color: white;
}

.chatting-tag {
  font-size: 11px;
  color: #67C23A;
  font-weight: 500;
  background: rgba(103, 194, 58, 0.1);
  padding: 2px 6px;
  border-radius: 10px;
}

/* AI助手样式 */
.ai-assistant {
  margin-top: 20px;
  border-top: 1px solid rgba(235, 238, 245, 0.5);
  padding-top: 20px;
}

.ai-header {
  padding: 15px;
  border-radius: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.5);
}

.ai-header:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(5px);
}

.ai-avatar {
  position: relative;
  flex-shrink: 0;
}

.ai-avatar-bg {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  border: 2px solid rgba(255, 255, 255, 0.8);
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

.ai-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ai-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.ai-subtitle {
  font-size: 12px;
  color: #909399;
}

.ai-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.ai-icon {
  font-size: 16px;
  color: #667eea;
  transition: all 0.3s ease;
}

.ai-tag {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

/* 历史记录样式 */
.ai-history {
  margin-top: 15px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 0 12px;
}

.history-title {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}

.clear-btn {
  color: #909399;
}

.clear-btn:hover {
  color: #F56C6C;
}

.history-list {
  max-height: 200px;
  overflow-y: auto;
  padding: 0 12px;
}

.history-item {
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
}

.history-item:hover {
  background-color: #f5f7fa;
}

.history-item.active {
  background-color: #ecf5ff;
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
}

.history-time {
  font-size: 12px;
  color: #909399;
}

/* 消息区域样式 */
.message-container {
  height: calc(100% - 180px);
  padding: 20px;
  overflow-y: auto;
  background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%);
}

.welcome-message {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-content {
  text-align: center;
  color: #909399;
  max-width: 500px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.welcome-logo {
  width: 100px;
  height: auto;
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.welcome-content h2 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 10px 0;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-content p {
  font-size: 16px;
  margin: 0 0 30px 0;
  color: #606266;
}

.welcome-features {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin: 30px 0;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  border-radius: 15px;
  background: rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.feature-item:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-5px);
}

.feature-item i {
  font-size: 24px;
  color: #667eea;
}

.feature-item span {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.welcome-tip {
  font-size: 14px;
  color: #909399;
  margin: 20px 0 0 0;
  padding: 10px 20px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 20px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.loading-message {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
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

/* 输入区域样式 */
.input-area {
  padding: 20px;
  border-top: 1px solid rgba(235, 238, 245, 0.5);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

.input-wrapper {
  position: relative;
}

.input-container {
  position: relative;
  margin-bottom: 15px;
}

.message-input {
  width: 100%;
  min-height: 80px;
  padding: 15px 50px 15px 15px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 15px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.message-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  outline: none;
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

/* 消息气泡样式 */
:deep(.tip) {
  max-width: 80%;
  padding: 15px 20px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  position: relative;
  margin: 8px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

:deep(.tip.right) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-left: auto;
  border-top-right-radius: 6px;
}

:deep(.tip.left) {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: white;
  margin-right: auto;
  border-top-left-radius: 6px;
}

:deep(.tip.right[style*="background-color: #a0d8ef"]) {
  background: linear-gradient(135deg, #a0d8ef 0%, #b8e6f5 100%) !important;
  color: #303133;
}

/* 图片消息样式 */
:deep(.image-message) {
  max-width: 300px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.image-message img) {
  width: 100%;
  height: auto;
  display: block;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 空状态样式 */
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #909399;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 32px;
  color: #667eea;
}

.empty-state p {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #606266;
}

.empty-tip {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 添加打字机效果相关样式 */
.typing-message {
  position: relative;
  min-height: 20px;
}

.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 1em;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin-left: 2px;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 添加消息动画效果 */
:deep(.el-row) {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    padding: 10px;
  }
  
  .user-list-card, .chat-card {
    height: calc(100vh - 100px);
  }
  
  .welcome-features {
    flex-direction: column;
    gap: 15px;
  }
  
  .feature-item {
    flex-direction: row;
    justify-content: flex-start;
    text-align: left;
  }
  
  .welcome-content {
    padding: 20px;
    margin: 10px;
  }
  
  .welcome-content h2 {
    font-size: 24px;
  }
}
</style>