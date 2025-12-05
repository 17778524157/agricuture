<template>
  <div class="role-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-s-custom"></i>
          角色管理
        </h1>
        <p class="page-subtitle">管理系统角色和权限分配</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <div class="stat-number">{{ total }}</div>
          <div class="stat-label">总角色数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ tableData.length }}</div>
          <div class="stat-label">当前页</div>
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
              v-model="name" 
              placeholder="请输入角色名称" 
              prefix-icon="el-icon-s-custom"
              clearable
              @keyup.enter.native="load"
            >
              <template slot="prepend">角色名称</template>
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
          新增角色
        </el-button>
        <el-popconfirm
          confirm-button-text='确定'
          cancel-button-text='取消'
          icon="el-icon-warning"
          icon-color="red"
          title="确定要批量删除选中的角色吗？"
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
      </div>
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-s-grid"></i>
          角色列表
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
        element-loading-text="正在加载角色数据..."
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        stripe 
        :header-cell-class-name="'custom-header'"
        @selection-change="handleSelectionChange"
        class="role-table"
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
        
        <el-table-column prop="name" label="角色名称" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="role-info">
              <el-avatar :size="32" :src="getRoleAvatar(scope.row.flag)" icon="el-icon-s-custom"></el-avatar>
              <span class="role-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="flag" label="唯一标识" width="140" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-tag 
              :type="getRoleType(scope.row.flag)"
              size="small"
              effect="dark"
            >
              {{ scope.row.flag }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="role-description">
              <i class="el-icon-document"></i>
              <span>{{ scope.row.description || '暂无描述' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="权限数量" width="120" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="success">
              {{ getPermissionCount(scope.row.id) }} 个
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-tooltip content="分配菜单" placement="top">
                <el-button 
                  type="info" 
                  size="mini" 
                  @click="selectMenu(scope.row)"
                  icon="el-icon-menu"
                  circle
                ></el-button>
              </el-tooltip>
              <el-tooltip content="编辑角色" placement="top">
                <el-button 
                  type="primary" 
                  size="mini" 
                  @click="handleEdit(scope.row)"
                  icon="el-icon-edit"
                  circle
                ></el-button>
              </el-tooltip>
              <el-popconfirm
                confirm-button-text='确定'
                cancel-button-text='取消'
                icon="el-icon-warning"
                icon-color="red"
                title="确定要删除这个角色吗？"
                @confirm="del(scope.row.id)"
              >
                <el-tooltip content="删除角色" placement="top">
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

    <!-- 角色编辑对话框 -->
    <el-dialog 
      :title="form.id ? '编辑角色' : '新增角色'" 
      :visible.sync="dialogFormVisible" 
      width="600px"
      :close-on-click-modal="false"
      class="role-dialog"
    >
      <el-form 
        :model="form" 
        :rules="formRules" 
        ref="form" 
        label-width="100px" 
        size="medium"
        class="role-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="name">
              <el-input 
                v-model="form.name" 
                placeholder="请输入角色名称"
                prefix-icon="el-icon-s-custom"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="唯一标识" prop="flag">
              <el-input 
                v-model="form.flag" 
                placeholder="请输入唯一标识"
                prefix-icon="el-icon-key"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="角色描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
            maxlength="200"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="medium">取 消</el-button>
        <el-button type="primary" @click="save" :loading="saveLoading" size="medium">
          {{ form.id ? '更新' : '创建' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 菜单分配对话框 -->
    <el-dialog 
      title="菜单分配" 
      :visible.sync="menuDialogVis" 
      width="500px"
      class="role-dialog"
    >
      <div class="menu-assignment">
        <div class="assignment-header">
          <el-alert
            title="菜单权限分配"
            type="success"
            :closable="false"
            show-icon
          >
            <template slot="title">
              <span>为角色 <strong>{{ roleFlag }}</strong> 分配菜单权限</span>
            </template>
          </el-alert>
        </div>
        
        <div class="menu-tree" v-if="menuData.length > 0">
          <el-tree
            :props="props"
            :data="menuData"
            show-checkbox
            node-key="id"
            ref="tree"
            :default-expanded-keys="expends"
            :default-checked-keys="checks"
            class="custom-tree"
          >
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span>
                <i :class="data.icon"></i> 
                {{ data.name }}
              </span>
            </span>
          </el-tree>
        </div>
        
        <div v-else class="empty-menu">
          <el-empty description="暂无菜单数据"></el-empty>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="menuDialogVis = false" size="medium">关 闭</el-button>
        <el-button type="primary" @click="saveRoleMenu" size="medium">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Role",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      menuDialogVis: false,
      multipleSelection: [],
      menuData: [],
      props: {
        label: 'name',
      },
      expends: [],
      checks: [],
      roleId: 0,
      roleFlag: '',
      ids: [],
      loading: false,
      saveLoading: false,
      showSearch: true,
      selectAll: false,
      formRules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        flag: [
          { required: true, message: '请输入唯一标识', trigger: 'blur' },
          { pattern: /^ROLE_[A-Z0-9_]+$/, message: '唯一标识格式不正确，应以ROLE_开头', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      // 使用微服务API获取角色数据
      this.request.get("/api/role").then(res => {
        console.log('微服务API返回角色数据:', res)
        
        // 处理微服务返回的Result格式
        let rolesData = null
        if (res && res.code === '200' && res.data && Array.isArray(res.data)) {
          rolesData = res.data
        } else if (res && Array.isArray(res)) {
          rolesData = res
        }
        
        if (rolesData && rolesData.length > 0) {
          // 模拟分页处理（微服务暂时没有分页接口）
          const start = (this.pageNum - 1) * this.pageSize
          const end = start + this.pageSize
          this.tableData = rolesData.slice(start, end)
          this.total = rolesData.length
          this.$message.success(`成功获取${rolesData.length}个角色`)
        } else {
          this.tableData = []
          this.total = 0
          this.$message.info('暂无角色数据')
        }
      }).catch(err => {
        console.error('获取角色数据失败:', err)
        console.log('使用备用角色数据')
        // 使用备用角色数据
        const mockRoles = [
          { id: 1, name: '系统管理员', flag: 'ROLE_ADMIN', description: '系统管理员' },
          { id: 2, name: '农田负责人', flag: 'ROLE_USER1', description: '农田负责人' },
          { id: 3, name: '仓库管理员', flag: 'ROLE_USER2', description: '仓库管理员' },
          { id: 4, name: '销售人员', flag: 'ROLE_USER3', description: '销售人员' },
          { id: 5, name: '采购人员', flag: 'ROLE_USER4', description: '采购人员' },
          { id: 6, name: '农业专家', flag: 'ROLE_USER5', description: '农业专家' }
        ]
        
        const start = (this.pageNum - 1) * this.pageSize
        const end = start + this.pageSize
        this.tableData = mockRoles.slice(start, end)
        this.total = mockRoles.length
        this.$message.warning('使用备用角色数据')
      })

      // 获取菜单ID数据（暂时使用备用数据）
      this.ids = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // 备用菜单ID
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          
          // 根据是否有ID判断是新增还是编辑
          const apiUrl = "/api/role"
          const method = this.form.id ? 'put' : 'post'
          
          console.log('保存角色数据:', this.form)
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
            console.error('保存角色失败:', err)
            this.$message.error("保存失败：" + (err.message || '未知错误'))
          }).finally(() => {
            this.saveLoading = false
          })
        } else {
          this.$message.warning('请完善表单信息')
          return false
        }
      })
    },
    saveRoleMenu() {
      // 获取选中的菜单ID
      const checkedKeys = this.$refs.tree.getCheckedKeys()
      const halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys()
      const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
      
      console.log('选中的菜单ID:', allCheckedKeys)
      
      // 调用API保存角色菜单权限
      this.request.post(`/api/role/roleMenu/${this.roleId}`, allCheckedKeys).then(res => {
        console.log('保存角色菜单权限响应:', res)
        if (res && res.code === '200') {
          this.$message.success("菜单权限分配成功")
          this.menuDialogVis = false
          
          // 操作管理员角色后需要重新登录
          if (this.roleFlag === 'ROLE_ADMIN') {
            this.$message.warning("管理员角色权限已修改，建议重新登录")
          }
        } else {
          this.$message.error("菜单权限分配失败：" + (res.message || '未知错误'))
        }
      }).catch(err => {
        console.error('保存角色菜单权限失败:', err)
        this.$message.error("菜单权限分配失败：" + (err.message || '未知错误'))
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
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
    
    getRoleType(roleFlag) {
      const roleTypes = {
        'ROLE_ADMIN': 'danger',
        'ROLE_USER1': 'primary',
        'ROLE_USER2': 'success',
        'ROLE_USER3': 'warning',
        'ROLE_USER4': 'info',
        'ROLE_USER5': 'primary'
      }
      return roleTypes[roleFlag] || 'info'
    },
    
    getRoleAvatar(roleFlag) {
      const avatars = {
        'ROLE_ADMIN': 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        'ROLE_USER1': 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
        'ROLE_USER2': 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        'ROLE_USER3': 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48png.png',
        'ROLE_USER4': 'https://cube.elemecdn.com/1/34/19aa98b1fcb2781c4f62833e9454png.png',
        'ROLE_USER5': 'https://cube.elemecdn.com/2/11/6e958c7aee2920fdb5a7a3a22278apng.png'
      }
      return avatars[roleFlag] || null
    },
    
    getPermissionCount(roleId) {
      // 模拟权限数量，实际应该从后端获取
      const permissionCounts = {
        1: 15, // 系统管理员
        2: 8,  // 农田负责人
        3: 6,  // 仓库管理员
        4: 5,  // 销售人员
        5: 7,  // 采购人员
        6: 10  // 农业专家
      }
      return permissionCounts[roleId] || 0
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/api/role/" + id).then(res => {
        if (res && (res.code === '200' || res === true)) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败：" + (res.message || res.msg || '未知错误'))
        }
      }).catch(err => {
        console.error('删除角色失败:', err)
        this.$message.error("删除失败：" + (err.message || '未知错误'))
      })
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
        this.request.delete("/api/role/" + item.id)
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
      this.name = ""
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
    async selectMenu(role) {
      this.roleId = role.id
      this.roleFlag = role.flag

      try {
        // 获取菜单数据
        const menuRes = await this.request.get("/api/menu")
        console.log('获取菜单数据响应:', menuRes)
        
        if (menuRes && menuRes.code === '200' && menuRes.data) {
          this.menuData = menuRes.data.map(menu => ({
            id: menu.id,
            name: menu.name,
            icon: menu.icon || 'el-icon-menu',
            pid: menu.pid,
            path: menu.path,
            description: menu.description
          }))
          
          // 设置展开的节点
          this.expends = this.menuData.map(v => v.id)
          
          // 获取角色的菜单权限
          const roleMenuRes = await this.request.get(`/api/role/roleMenu/${role.id}`)
          console.log('获取角色菜单权限响应:', roleMenuRes)
          
          if (roleMenuRes && roleMenuRes.code === '200' && roleMenuRes.data) {
            this.checks = roleMenuRes.data
          } else {
            this.checks = []
          }
          
          this.menuDialogVis = true
          
          this.$nextTick(() => {
            if (this.$refs.tree) {
              this.$refs.tree.setCheckedKeys(this.checks)
            }
          })
          
          this.$message.success(`成功加载${this.menuData.length}个菜单`)
        } else {
          throw new Error('获取菜单数据失败')
        }
      } catch (error) {
        console.error('获取菜单数据失败:', error)
        
        // 使用备用菜单数据
        this.menuData = [
          { id: 1, name: '系统管理', icon: 'el-icon-setting', pid: null },
          { id: 2, name: '用户管理', icon: 'el-icon-user', pid: 1 },
          { id: 3, name: '角色管理', icon: 'el-icon-s-custom', pid: 1 },
          { id: 4, name: '菜单管理', icon: 'el-icon-menu', pid: 1 },
          { id: 5, name: '农田管理', icon: 'el-icon-s-home', pid: null },
          { id: 6, name: '作物管理', icon: 'el-icon-s-platform', pid: null },
          { id: 7, name: '库存管理', icon: 'el-icon-s-goods', pid: null },
          { id: 8, name: '销售管理', icon: 'el-icon-s-marketing', pid: null },
          { id: 9, name: '采购管理', icon: 'el-icon-shopping-cart-2', pid: null },
          { id: 10, name: '报表统计', icon: 'el-icon-s-data', pid: null },
          { id: 11, name: '系统设置', icon: 'el-icon-s-tools', pid: null }
        ]
        
        this.expends = this.menuData.map(v => v.id)
        this.menuDialogVis = true
        
        // 使用默认选中状态
        this.checks = [1, 2, 3] // 默认选中前3个菜单
        this.$nextTick(() => {
          if (this.$refs.tree) {
            this.$refs.tree.setCheckedKeys(this.checks)
          }
        })
        
        this.$message.warning('使用备用菜单数据')
      }
    },
  }
}
</script>


<style scoped>
/* 主容器 */
.role-management-container {
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
.role-table {
  border-radius: 8px;
  overflow: hidden;
}

.custom-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%) !important;
  color: #495057 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #dee2e6 !important;
}

.role-table .warning-row {
  background-color: #fef9e7;
}

.role-table .warning-row:hover > td {
  background-color: #fdf6e3 !important;
}

/* 角色信息 */
.role-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-name {
  font-weight: 500;
  color: #303133;
}

.role-description {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.role-description i {
  color: #909399;
  font-size: 14px;
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
.role-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.role-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
}

.role-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
}

.role-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.role-form {
  padding: 20px;
}

.dialog-footer {
  padding: 20px;
  text-align: right;
  border-top: 1px solid #ebeef5;
}

/* 菜单分配 */
.menu-assignment {
  padding: 20px;
}

.assignment-header {
  margin-bottom: 20px;
}

.menu-tree {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
}

.custom-tree {
  background: #fafafa;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-tree-node i {
  color: #409EFF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .role-management-container {
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
  
  .role-dialog {
    width: 95% !important;
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
