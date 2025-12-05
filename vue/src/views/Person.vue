<template>
  <div class="person-container">
    <el-card class="person-card" :body-style="{ padding: '0px' }">
      <div class="card-header">
        <h2 class="title">个人中心</h2>
        <p class="subtitle">管理您的个人信息和账户设置</p>
      </div>
      
      <div class="card-content">
        <div class="avatar-section">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/api/avatar/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :headers="uploadHeaders"
          >
            <div class="avatar-wrapper">
              <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
              <div v-else class="avatar-placeholder">
                <i class="el-icon-user-solid"></i>
                <span>点击上传头像</span>
              </div>
              <div class="avatar-hover-mask">
                <i class="el-icon-camera"></i>
                <span>更换头像</span>
              </div>
            </div>
          </el-upload>
          <div class="user-info">
            <h3 class="username">{{ form.nickname || form.username }}</h3>
            <p class="user-role">{{ form.role === 'ROLE_ADMIN' ? '管理员' : '普通用户' }}</p>
          </div>
        </div>

        <el-form 
          label-width="80px" 
          size="small" 
          class="info-form"
          :model="form"
          :rules="rules"
          ref="form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="form.username" 
              disabled 
              prefix-icon="el-icon-user"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input 
              v-model="form.nickname" 
              prefix-icon="el-icon-edit"
              placeholder="请输入您的昵称"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="form.email" 
              prefix-icon="el-icon-message"
              placeholder="请输入您的邮箱"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="电话" prop="phone">
            <el-input 
              v-model="form.phone" 
              prefix-icon="el-icon-phone"
              placeholder="请输入您的电话号码"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="地址" prop="address">
            <el-input 
              type="textarea" 
              v-model="form.address" 
              :rows="3"
              placeholder="请输入您的详细地址"
            ></el-input>
          </el-form-item>
          
          <el-form-item class="form-buttons">
            <el-button 
              type="primary" 
              @click="save" 
              :loading="loading"
              class="save-button"
            >
              <i class="el-icon-check"></i>
              保存修改
            </el-button>
            <el-button 
              @click="resetForm" 
              class="reset-button"
            >
              <i class="el-icon-refresh"></i>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "Person",
  data() {
    // 邮箱验证规则
    const validateEmail = (rule, value, callback) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (value && !emailRegex.test(value)) {
        callback(new Error('请输入有效的邮箱地址'));
      } else {
        callback();
      }
    };
    
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (value && !phoneRegex.test(value)) {
        callback(new Error('请输入有效的手机号码'));
      } else {
        callback();
      }
    };

    return {
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      loading: false,
      uploadHeaders: {
        'Authorization': localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).token : '',
        'userId': localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        address: [
          { max: 200, message: '地址不能超过200个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getUser().then(res => {
      console.log(res)
      this.form = res
    })
  },
  methods: {
    async getUser() {
      return (await this.request.get("/user/username/" + this.user.username)).data
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true;
          this.request.post("/user", this.form).then(res => {
            if (res.code === '200') {
              this.$message({
                message: "个人信息更新成功",
                type: "success",
                duration: 2000
              });

              // 触发父级更新User的方法
              this.$emit("refreshUser");

              // 更新浏览器存储的用户信息
              this.getUser().then(res => {
                res.token = JSON.parse(localStorage.getItem("user")).token;
                localStorage.setItem("user", JSON.stringify(res));
              });
            } else {
              this.$message({
                message: "保存失败，请重试",
                type: "error",
                duration: 2000
              });
            }
          }).finally(() => {
            this.loading = false;
          });
        } else {
          this.$message({
            message: "请检查表单填写是否正确",
            type: "warning",
            duration: 2000
          });
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.form.resetFields();
      this.getUser().then(res => {
        this.form = res;
      });
      this.$message({
        message: "表单已重置",
        type: "info",
        duration: 1500
      });
    },
    handleAvatarSuccess(res) {
      if (res.code === '200' || res.code === 200) {
        // 从响应中获取头像URL
        let avatarUrl = '';
        if (typeof res.data === 'string') {
          avatarUrl = res.data;
        } else if (res.data && res.data.url) {
          avatarUrl = res.data.url;
        }
        
        this.form.avatarUrl = avatarUrl;
        this.$message({
          message: "头像上传成功",
          type: "success",
          duration: 1500
        });
      } else {
        this.$message({
          message: res.message || "头像上传失败",
          type: "error",
          duration: 2000
        });
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }
  }
}
</script>

<style scoped>
.person-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
}

.person-card {
  width: 100%;
  max-width: 600px;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
}

.person-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  background: linear-gradient(135deg, #6B8DD6 0%, #8E37D7 100%);
  color: white;
  padding: 30px;
  text-align: center;
}

.title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 1px;
}

.subtitle {
  margin: 10px 0 0;
  font-size: 16px;
  opacity: 0.9;
}

.card-content {
  padding: 30px;
  background: white;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-wrapper {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  color: #909399;
}

.avatar-placeholder i {
  font-size: 40px;
  margin-bottom: 8px;
}

.avatar-placeholder span {
  font-size: 14px;
}

.avatar-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  opacity: 0;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .avatar-hover-mask {
  opacity: 1;
}

.avatar-hover-mask i {
  font-size: 24px;
  margin-bottom: 8px;
}

.user-info {
  text-align: center;
  margin-top: 15px;
}

.username {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.user-role {
  margin: 5px 0 0;
  font-size: 14px;
  color: #909399;
}

.info-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-input {
  transition: all 0.3s ease;
}

.el-input:hover .el-input__inner {
  border-color: #8E37D7;
}

.el-input__inner {
  border-radius: 8px;
  padding: 10px 15px;
  height: 40px;
  transition: all 0.3s ease;
}

.el-textarea__inner {
  border-radius: 8px;
  padding: 10px 15px;
  transition: all 0.3s ease;
}

.form-buttons {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.save-button, .reset-button {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.save-button {
  background: linear-gradient(135deg, #6B8DD6 0%, #8E37D7 100%);
  border: none;
  margin-right: 15px;
}

.save-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(142, 55, 215, 0.3);
}

.reset-button {
  background: #f5f7fa;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.reset-button:hover {
  background: #e4e8f0;
  color: #303133;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .person-card {
    max-width: 100%;
  }
  
  .card-header {
    padding: 20px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .card-content {
    padding: 20px;
  }
  
  .avatar-wrapper {
    width: 120px;
    height: 120px;
  }
  
  .form-buttons {
    flex-direction: column;
  }
  
  .save-button, .reset-button {
    width: 100%;
    margin: 10px 0;
  }
}
</style>
