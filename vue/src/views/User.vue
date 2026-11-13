<template>
  <div class="user-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-user"></i>
          用户管理
        </h1>
        <p class="page-subtitle">管理系统用户信息和权限</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <div class="stat-number">{{ total }}</div>
          <div class="stat-label">总用户数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ roles.length }}</div>
          <div class="stat-label">角色类型</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-search"></i>
          搜索筛选
        </span>
        <el-button type="text" @click="toggleSearch" class="toggle-btn">
          {{ showSearch ? '收起' : '展开' }}
          <i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </el-button>
      </div>
      
      <div class="search-form" v-show="showSearch">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-input 
              v-model="username" 
              placeholder="请输入用户名" 
              prefix-icon="el-icon-user"
              clearable
              @keyup.enter.native="load"
            >
              <template slot="prepend">用户名</template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-input 
              v-model="email" 
              placeholder="请输入邮箱" 
              prefix-icon="el-icon-message"
              clearable
              @keyup.enter.native="load"
            >
              <template slot="prepend">邮箱</template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-input 
              v-model="address" 
              placeholder="请输入地址" 
              prefix-icon="el-icon-location"
              clearable
              @keyup.enter.native="load"
            >
              <template slot="prepend">地址</template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="search-buttons">
              <el-button type="primary" @click="load" :loading="loading" icon="el-icon-search">
                搜索
              </el-button>
              <el-button @click="reset" icon="el-icon-refresh">
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd" icon="el-icon-plus" class="action-btn">
          新增用户
        </el-button>
        <el-popconfirm
          confirm-button-text='确定'
          cancel-button-text='取消'
          icon="el-icon-warning"
          icon-color="red"
          title="确定要批量删除选中的用户吗？"
          @confirm="delBatch"
        >
          <el-button 
            type="danger" 
            slot="reference" 
            icon="el-icon-delete" 
            :disabled="!multipleSelection.length"
            class="action-btn"
          >
            批量删除 ({{ multipleSelection.length }})
          </el-button>
        </el-popconfirm>
        <el-upload 
          action="http://localhost:8080/api/user/import" 
          :show-file-list="false" 
          accept="xlsx" 
          :on-success="handleExcelImportSuccess"
          class="upload-btn"
        >
          <!-- 【改造】停用"导入用户"：本系统用户管理只保留 新增/编辑/删除/分配角色/重置密码 -->
          <el-button v-if="false" type="success" icon="el-icon-upload" class="action-btn">
            导入用户
          </el-button>
        </el-upload>
        <!-- 【改造】停用"导出数据"：不在保留的用户管理功能范围内 -->
        <el-button v-if="false" type="warning" @click="exp" icon="el-icon-download" class="action-btn">
          导出数据
        </el-button>
      </div>
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-s-grid"></i>
          用户列表
        </span>
        <div class="table-actions">
          <el-tooltip content="刷新数据" placement="top">
            <el-button type="text" @click="load" :loading="loading" icon="el-icon-refresh"></el-button>
          </el-tooltip>
          <el-tooltip content="列设置" placement="top">
            <el-button type="text" icon="el-icon-setting"></el-button>
          </el-tooltip>
        </div>
      </div>
      
      <el-table 
        :data="tableData" 
        v-loading="loading"
        element-loading-text="正在加载用户数据..."
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        stripe 
        :header-cell-class-name="'custom-header'"
        @selection-change="handleSelectionChange"
        class="user-table"
        :row-class-name="getRowClassName"
      >
        <el-table-column type="selection" width="55" align="center">
          <template slot="header">
            <el-checkbox v-model="selectAll" @change="handleSelectAll"></el-checkbox>
          </template>
        </el-table-column>
        
        <el-table-column prop="id" label="ID" width="80" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="info">{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="username" label="用户名" width="140" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="user-info">
              <el-avatar :size="32" :src="scope.row.avatar" icon="el-icon-user-solid"></el-avatar>
              <span class="username">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="角色" width="120" align="center">
          <template slot-scope="scope">
            <el-tag 
              v-if="scope.row.role" 
              :type="getRoleType(scope.row.role)"
              size="small"
            >
              {{ getRoleName(scope.row.role) }}
            </el-tag>
            <span v-else class="no-role">未分配</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="nickname" label="昵称" width="120" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.nickname">{{ scope.row.nickname }}</span>
            <span v-else class="no-data">未设置</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="contact-info">
              <i class="el-icon-message"></i>
              <span>{{ scope.row.email || '未设置' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="phone" label="电话" width="140" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="contact-info">
              <i class="el-icon-phone"></i>
              <span>{{ scope.row.phone || '未设置' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="address-info">
              <i class="el-icon-location"></i>
              <span>{{ scope.row.address || '未设置' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-tooltip content="编辑用户" placement="top">
                <el-button 
                  type="primary" 
                  size="mini" 
                  @click="handleEdit(scope.row)"
                  icon="el-icon-edit"
                  circle
                ></el-button>
              </el-tooltip>
              <!-- 【改造】停用"查看详情"：保留功能不含此项 -->
              <el-tooltip v-if="false" content="查看详情" placement="top">
                <el-button 
                  type="info" 
                  size="mini" 
                  @click="handleView(scope.row)"
                  icon="el-icon-view"
                  circle
                ></el-button>
              </el-tooltip>
              <!-- 【改造-新增】重置密码：管理员把该用户密码重置为默认密码 123456 -->
              <el-tooltip content="重置密码" placement="top">
                <el-button
                  type="warning"
                  size="mini"
                  @click="resetPwd(scope.row)"
                  icon="el-icon-key"
                  circle
                ></el-button>
              </el-tooltip>
              <el-popconfirm
                confirm-button-text='确定'
                cancel-button-text='取消'
                icon="el-icon-warning"
                icon-color="red"
                title="确定要删除这个用户吗？"
                @confirm="del(scope.row.id)"
              >
                <el-tooltip content="删除用户" placement="top">
                  <el-button 
                    type="danger" 
                    size="mini" 
                    slot="reference"
                    icon="el-icon-delete"
                    circle
                  ></el-button>
                </el-tooltip>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 分页区域 -->
    <el-card class="pagination-card" shadow="hover">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        class="pagination"
      >
      </el-pagination>
    </el-card>

    <!-- 用户编辑对话框 -->
    <el-dialog 
      :title="form.id ? '编辑用户' : '新增用户'" 
      :visible.sync="dialogFormVisible" 
      width="600px"
      :close-on-click-modal="false"
      class="user-dialog"
    >
      <el-form 
        :model="form" 
        :rules="formRules" 
        ref="form" 
        label-width="100px" 
        size="medium"
        class="user-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select 
                v-model="form.role" 
                placeholder="请选择角色" 
                style="width: 100%"
                clearable
              >
                <el-option 
                  v-for="item in roles" 
                  :key="item.flag" 
                  :label="item.name" 
                  :value="item.flag"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input 
                v-model="form.nickname" 
                placeholder="请输入昵称"
                prefix-icon="el-icon-user"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input 
                v-model="form.phone" 
                placeholder="请输入电话号码"
                prefix-icon="el-icon-phone"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱地址"
            prefix-icon="el-icon-message"
            clearable
          ></el-input>
        </el-form-item>
        
        <el-form-item label="地址" prop="address">
          <el-input 
            v-model="form.address" 
            placeholder="请输入地址"
            prefix-icon="el-icon-location"
            clearable
          ></el-input>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="medium">取 消</el-button>
        <el-button type="primary" @click="save" :loading="saveLoading" size="medium">
          {{ form.id ? '更新' : '创建' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog 
      title="用户详情" 
      :visible.sync="viewDialogVisible" 
      width="500px"
      class="user-dialog"
    >
      <div class="user-detail" v-if="viewUser">
        <div class="detail-header">
          <el-avatar :size="80" :src="viewUser.avatar" icon="el-icon-user-solid"></el-avatar>
          <div class="user-basic">
            <h3>{{ viewUser.username }}</h3>
            <p>{{ viewUser.nickname || '未设置昵称' }}</p>
            <el-tag :type="getRoleType(viewUser.role)" size="medium">
              {{ getRoleName(viewUser.role) }}
            </el-tag>
          </div>
        </div>
        
        <el-divider></el-divider>
        
        <div class="detail-content">
          <div class="detail-item">
            <label>用户ID:</label>
            <span>{{ viewUser.id }}</span>
          </div>
          <div class="detail-item">
            <label>邮箱:</label>
            <span>{{ viewUser.email || '未设置' }}</span>
          </div>
          <div class="detail-item">
            <label>电话:</label>
            <span>{{ viewUser.phone || '未设置' }}</span>
          </div>
          <div class="detail-item">
            <label>地址:</label>
            <span>{{ viewUser.address || '未设置' }}</span>
          </div>
          <div class="detail-item">
            <label>状态:</label>
            <el-tag :type="viewUser.status === 'active' ? 'success' : 'danger'">
              {{ viewUser.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false" size="medium">关 闭</el-button>
        <el-button type="primary" @click="handleEdit(viewUser)" size="medium">编 辑</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "User",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      username: "",
      email: "",
      address: "",
      form: {},
      dialogFormVisible: false,
      viewDialogVisible: false,
      viewUser: null,
      multipleSelection: [],
      roles: [],
      loading: false,
      saveLoading: false,
      showSearch: true,
      selectAll: false,
      formRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      // 使用微服务API获取用户数据
      this.request.get("/api/user/list").then(res => {
        console.log('微服务API返回用户数据:', res)
        
        // 处理微服务返回的Result格式
        let usersData = null
        if (res && res.code === '200' && res.data && Array.isArray(res.data)) {
          usersData = res.data
        } else if (res && Array.isArray(res)) {
          usersData = res
        }
        
        if (usersData && usersData.length > 0) {
          // 模拟分页处理（微服务暂时没有分页接口）
          const start = (this.pageNum - 1) * this.pageSize
          const end = start + this.pageSize
          this.tableData = usersData.slice(start, end)
          this.total = usersData.length
          this.$message.success(`成功获取${usersData.length}条用户数据`)
        } else {
          this.tableData = []
          this.total = 0
          this.$message.info('暂无用户数据')
        }
      }).catch(err => {
        console.error('获取用户数据失败:', err)
        this.$message.error('获取用户数据失败: ' + (err.message || '网络错误'))
        this.tableData = []
        this.total = 0
      })

      // 获取角色数据
      this.request.get("/api/role").then(res => {
        console.log('微服务API返回角色数据:', res)
        if (res && res.code === '200' && res.data && Array.isArray(res.data)) {
          this.roles = res.data
          this.$message.success(`成功获取${res.data.length}个角色`)
        } else if (Array.isArray(res)) {
          this.roles = res
        }
      }).catch(err => {
        console.error('获取角色数据失败:', err)
        console.log('使用备用角色数据')
        // 使用备用角色数据
        // 【改造】备用角色只保留 3 个（正常应来自 /api/role）
        this.roles = [
          { id: 1, name: '系统管理员', flag: 'ROLE_ADMIN', description: '拥有全部权限' },
          { id: 2, name: '仓库管理员', flag: 'ROLE_WAREHOUSE', description: '入库/库存/盘点/出库' },
          { id: 3, name: '销售业务员', flag: 'ROLE_SALES', description: '客户管理/销售订单' }
        ]
        this.$message.warning('使用备用角色数据')
      })
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          
          // 根据是否有ID判断是新增还是编辑
          const apiUrl = "/api/user"
          const method = this.form.id ? 'put' : 'post'
          
          console.log('保存用户数据:', this.form)
          console.log('API方法:', method)
          
          this.request[method](apiUrl, this.form).then(res => {
            console.log('保存响应:', res)
            if (res && (res.code === '200' || res === true)) {
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.load()
            } else {
              this.$message.error("保存失败：" + (res.message || res.msg || '未知错误'))
            }
          }).catch(err => {
            console.error('保存用户失败:', err)
            console.error('错误详情:', {
              message: err.message,
              response: err.response,
              status: err.response?.status,
              data: err.response?.data
            })
            
            // 根据错误类型显示不同的消息
            if (err.response?.status === 401) {
              this.$message.error('未授权访问，请检查登录状态')
            } else if (err.response?.status === 404) {
              this.$message.error('API接口不存在，请检查服务配置')
            } else if (err.response?.status === 500) {
              this.$message.error('服务器内部错误：' + (err.response?.data?.message || '请联系管理员'))
            } else if (err.code === 'ECONNREFUSED') {
              this.$message.error('无法连接到服务器，请检查服务是否启动')
            } else {
              this.$message.error("保存失败：" + (err.message || '未知错误'))
            }
          }).finally(() => {
            this.saveLoading = false
          })
        } else {
          this.$message.warning('请完善表单信息')
          return false
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {
        status: 'active'
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    
    handleView(row) {
      this.viewUser = row
      this.viewDialogVisible = true
    },
    
    toggleSearch() {
      this.showSearch = !this.showSearch
    },
    
    handleSelectAll(val) {
      if (val) {
        this.tableData.forEach(row => {
          this.$refs.table.toggleRowSelection(row, true)
        })
      } else {
        this.$refs.table.clearSelection()
      }
    },
    
    getRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 === 1) {
        return 'warning-row'
      }
      return ''
    },
    
    getRoleName(roleFlag) {
      const role = this.roles.find(r => r.flag === roleFlag)
      return role ? role.name : roleFlag
    },
    
    getRoleType(roleFlag) {
      // 【改造】按新的 3 个角色 flag 配色
      const roleTypes = {
        'ROLE_ADMIN': 'danger',
        'ROLE_WAREHOUSE': 'success',
        'ROLE_SALES': 'primary'
      }
      return roleTypes[roleFlag] || 'info'
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/api/user/" + id).then(res => {
        if (res && (res.code === '200' || res === true)) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      }).catch(err => {
        console.error('删除用户失败:', err)
        this.$message.error("删除失败：" + (err.message || '未知错误'))
      })
    },
    // 【改造-新增】重置指定用户密码（调用管理员重置接口）
    resetPwd(row) {
      this.$confirm(`确定将用户【${row.username}】的密码重置为默认密码 123456 吗？`, '重置密码', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.request.put("/api/user/resetPwd/" + row.id).then(res => {
          if (res.code === '200' || res.code === 200) {
            this.$message.success(res.message || "密码重置成功")
          } else {
            this.$message.error(res.message || res.msg || "重置失败")
          }
        }).catch(err => {
          this.$message.error("重置失败：" + (err.message || '未知错误'))
        })
      }).catch(() => {})
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      
      // 由于微服务可能不支持批量删除，我们逐个删除
      const deletePromises = this.multipleSelection.map(item => 
        this.request.delete("/api/user/" + item.id)
      )
      
      Promise.all(deletePromises).then(responses => {
        const successCount = responses.filter(res => res && (res.code === '200' || res === true)).length
        if (successCount === this.multipleSelection.length) {
          this.$message.success(`批量删除成功，共删除 ${successCount} 条记录`)
        } else {
          this.$message.warning(`部分删除成功，成功删除 ${successCount} 条记录`)
        }
        this.load()
      }).catch(err => {
        this.$message.error("批量删除失败：" + (err.message || '未知错误'))
      })
    },
    reset() {
      this.username = ""
      this.email = ""
      this.address = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    exp() {
      // 微服务暂时没有导出功能，提供数据下载替代方案
      if (this.tableData.length === 0) {
        this.$message.warning('暂无数据可导出')
        return
      }
      
      // 将表格数据转换为CSV格式并下载
      const csvContent = this.generateCSV(this.tableData)
      this.downloadCSV(csvContent, '用户数据.csv')
      this.$message.success('数据导出成功')
    },
    
    generateCSV(data) {
      const headers = ['ID', '用户名', '角色', '昵称', '邮箱', '电话', '地址']
      const csvRows = [headers.join(',')]
      
      data.forEach(row => {
        const roleName = this.roles.find(r => r.flag === row.role)?.name || row.role || ''
        const values = [
          row.id,
          `"${row.username || ''}"`,
          `"${roleName}"`,
          `"${row.nickname || ''}"`,
          `"${row.email || ''}"`,
          `"${row.phone || ''}"`,
          `"${row.address || ''}"`
        ]
        csvRows.push(values.join(','))
      })
      
      return csvRows.join('\n')
    },
    
    downloadCSV(content, filename) {
      const blob = new Blob(['\ufeff' + content], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', filename)
      link.style.visibility = 'hidden'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style scoped>
/* 主容器 */
.user-management-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.header-content h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

/* 卡片样式 */
.search-card,
.action-card,
.table-card,
.pagination-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-card:hover,
.action-card:hover,
.table-card:hover,
.pagination-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.toggle-btn {
  color: #409EFF;
  font-weight: 500;
}

/* 搜索表单 */
.search-form {
  padding-top: 20px;
}

.search-buttons {
  display: flex;
  gap: 12px;
  height: 40px;
  align-items: center;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 表格样式 */
.user-table {
  border-radius: 8px;
  overflow: hidden;
}

.custom-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%) !important;
  color: #495057 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #dee2e6 !important;
}

.user-table .warning-row {
  background-color: #fef9e7;
}

.user-table .warning-row:hover > td {
  background-color: #fdf6e3 !important;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 500;
  color: #303133;
}

.contact-info,
.address-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.contact-info i,
.address-info i {
  color: #909399;
  font-size: 14px;
}

.no-data,
.no-role {
  color: #c0c4cc;
  font-style: italic;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-buttons .el-button {
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: scale(1.1);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 对话框样式 */
.user-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.user-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
}

.user-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
}

.user-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.user-form {
  padding: 20px;
}

.dialog-footer {
  padding: 20px;
  text-align: right;
  border-top: 1px solid #ebeef5;
}

/* 用户详情 */
.user-detail {
  padding: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.user-basic h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 20px;
}

.user-basic p {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item label {
  font-weight: 600;
  color: #606266;
  min-width: 80px;
}

.detail-item span {
  color: #303133;
  flex: 1;
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .header-stats {
    gap: 20px;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .search-form .el-col {
    margin-bottom: 16px;
  }
  
  .search-buttons {
    justify-content: center;
  }
  
  .user-dialog {
    width: 95% !important;
  }
  
  .detail-header {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .action-buttons {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
  
  .search-buttons {
    flex-direction: column;
    height: auto;
  }
  
  .search-buttons .el-button {
    width: 100%;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-card,
.action-card,
.table-card,
.pagination-card {
  animation: fadeInUp 0.6s ease-out;
}

.search-card {
  animation-delay: 0.1s;
}

.action-card {
  animation-delay: 0.2s;
}

.table-card {
  animation-delay: 0.3s;
}

.pagination-card {
  animation-delay: 0.4s;
}

/* 加载状态 */
.el-loading-mask {
  border-radius: 8px;
}

/* 工具提示 */
.el-tooltip__popper {
  border-radius: 8px;
}

/* 标签样式 */
.el-tag {
  border-radius: 6px;
  font-weight: 500;
}

/* 输入框样式 */
.el-input__inner {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-input__inner:focus {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button--primary {
  background: linear-gradient(135deg, #409EFF 0%, #36a3f7 100%);
  border: none;
}

.el-button--success {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  border: none;
}

.el-button--warning {
  background: linear-gradient(135deg, #E6A23C 0%, #f0c78a 100%);
  border: none;
}

.el-button--danger {
  background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);
  border: none;
}

.el-button--info {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
  border: none;
}
</style>
