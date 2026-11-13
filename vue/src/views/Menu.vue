<template>
  <div class="menu-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-menu"></i>
          菜单管理
        </h1>
        <p class="page-subtitle">管理系统菜单结构和层级关系</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <div class="stat-number">{{ totalMenus }}</div>
          <div class="stat-label">总菜单数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ parentCount }}</div>
          <div class="stat-label">一级菜单</div>
        </div>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="name"
            placeholder="搜索菜单名称"
            prefix-icon="el-icon-search"
            clearable
            size="medium"
            class="search-input"
            @keyup.enter.native="load"
          ></el-input>
          <el-button type="primary" @click="load" :loading="loading" icon="el-icon-search" class="search-btn">
            搜索
          </el-button>
          <el-button @click="reset" icon="el-icon-refresh" class="reset-btn">
            重置
          </el-button>
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="handleAdd(null)" icon="el-icon-plus" class="add-btn">
            新增菜单
          </el-button>
          <el-popconfirm
            confirm-button-text='确定'
            cancel-button-text='取消'
            icon="el-icon-warning"
            icon-color="red"
            title="确定要批量删除选中的菜单吗？删除后子菜单也会被删除"
            @confirm="delBatch"
          >
            <el-button
              type="danger"
              slot="reference"
              icon="el-icon-delete"
              :disabled="!multipleSelection.length"
            >
              批量删除 ({{ multipleSelection.length }})
            </el-button>
          </el-popconfirm>
        </div>
      </div>
    </el-card>

    <!-- 菜单树表格 -->
    <el-card class="table-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-s-grid"></i>
          菜单列表
        </span>
        <div class="table-actions">
          <el-tooltip content="展开全部" placement="top">
            <el-button type="text" @click="expandAll" icon="el-icon-full-screen"></el-button>
          </el-tooltip>
          <el-tooltip content="收起全部" placement="top">
            <el-button type="text" @click="collapseAll" icon="el-icon-minus"></el-button>
          </el-tooltip>
          <el-tooltip content="刷新" placement="top">
            <el-button type="text" @click="load" :loading="loading" icon="el-icon-refresh"></el-button>
          </el-tooltip>
        </div>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="正在加载菜单数据..."
        row-key="id"
        border
        default-expand-all
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
        class="menu-table"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="70" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" type="info">{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜单名称" min-width="180">
          <template slot-scope="scope">
            <div class="menu-name-cell">
              <i v-if="scope.row.icon" :class="scope.row.icon" class="menu-icon"></i>
              <span class="menu-name">{{ scope.row.name }}</span>
              <el-tag v-if="!scope.row.children || scope.row.children.length === 0" size="mini" type="success" class="leaf-tag">
                叶子
              </el-tag>
              <el-tag v-else size="mini" type="warning" class="parent-tag">
                {{ scope.row.children.length }} 个子菜单
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.path" class="path-text">{{ scope.row.path }}</span>
            <span v-else class="no-path">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="pagePath" label="页面路径" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.pagePath" class="page-path-text">{{ scope.row.pagePath }}</span>
            <span v-else class="no-path">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template slot-scope="scope">
            <i v-if="scope.row.icon" :class="scope.row.icon" class="table-icon"></i>
            <span v-else class="no-icon">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortNum" label="排序" width="80" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="getSortTagType(scope.row.sortNum)">{{ scope.row.sortNum }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.description">{{ scope.row.description }}</span>
            <span v-else class="no-desc">暂无描述</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="row-actions">
              <el-tooltip content="新增子菜单" placement="top">
                <el-button
                  type="primary"
                  size="mini"
                  @click="handleAdd(scope.row.id)"
                  icon="el-icon-plus"
                  circle
                  class="action-btn"
                ></el-button>
              </el-tooltip>
              <el-tooltip content="编辑" placement="top">
                <el-button
                  type="success"
                  size="mini"
                  @click="handleEdit(scope.row)"
                  icon="el-icon-edit"
                  circle
                  class="action-btn"
                ></el-button>
              </el-tooltip>
              <el-popconfirm
                confirm-button-text='确定'
                cancel-button-text='取消'
                icon="el-icon-warning"
                icon-color="red"
                :title="scope.row.children && scope.row.children.length > 0
                    ? `确定删除吗？该菜单下有 ${scope.row.children.length} 个子菜单也会被删除`
                    : '确定删除这个菜单吗？'"
                @confirm="del(scope.row.id)"
              >
                <el-tooltip content="删除" placement="top" slot="reference">
                  <el-button
                    type="danger"
                    size="mini"
                    icon="el-icon-delete"
                    circle
                    class="action-btn"
                  ></el-button>
                </el-tooltip>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 菜单编辑对话框 -->
    <el-dialog
      :title="form.id ? '编辑菜单' : '新增菜单'"
      :visible.sync="dialogFormVisible"
      width="600px"
      :close-on-click-modal="false"
      custom-class="menu-dialog"
    >
      <el-form
        :model="form"
        :rules="formRules"
        ref="menuForm"
        label-width="100px"
        size="medium"
        class="menu-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="name">
              <el-input
                v-model="form.name"
                placeholder="请输入菜单名称"
                prefix-icon="el-icon-document"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上级菜单" prop="pid">
              <el-select
                v-model="form.pid"
                placeholder="选择上级菜单（顶级则不选）"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="menu in parentMenus"
                  :key="menu.id"
                  :label="menu.name"
                  :value="menu.id"
                >
                  <span style="display: flex; align-items: center; gap: 8px;">
                    <i v-if="menu.icon" :class="menu.icon" style="font-size: 14px;"></i>
                    <span>{{ menu.name }}</span>
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="路径" prop="path">
              <el-input
                v-model="form.path"
                placeholder="如：/user"
                prefix-icon="el-icon-link"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="页面路径" prop="pagePath">
              <el-input
                v-model="form.pagePath"
                placeholder="如：User"
                prefix-icon="el-icon-folder-opened"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-select
                v-model="form.icon"
                placeholder="请选择图标"
                clearable
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="item in iconOptions"
                  :key="item.value"
                  :label="item.name"
                  :value="item.value"
                >
                  <span style="display: flex; align-items: center; gap: 10px;">
                    <i :class="item.value" style="font-size: 16px; color: #67c23a;"></i>
                    <span>{{ item.name }}</span>
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortNum">
              <el-input-number
                v-model="form.sortNum"
                :min="0"
                :max="9999"
                size="medium"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入菜单描述"
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
  </div>
</template>

<script>
export default {
  name: "Menu",
  data() {
    return {
      tableData: [],
      allMenus: [],
      totalMenus: 0,
      parentCount: 0,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      loading: false,
      saveLoading: false,
      formRules: {
        name: [
          { required: true, message: '请输入菜单名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ]
      },
      iconOptions: [
        { value: 'el-icon-s-home', name: '首页' },
        { value: 'el-icon-menu', name: '菜单' },
        { value: 'el-icon-user', name: '用户' },
        { value: 'el-icon-s-custom', name: '角色' },
        { value: 'el-icon-s-grid', name: '网格' },
        { value: 'el-icon-s-goods', name: '商品' },
        { value: 'el-icon-s-order', name: '订单' },
        { value: 'el-icon-s-marketing', name: '营销' },
        { value: 'el-icon-s-data', name: '数据' },
        { value: 'el-icon-s-tools', name: '工具' },
        { value: 'el-icon-s-cooperation', name: '合作' },
        { value: 'el-icon-s-platform', name: '平台' },
        { value: 'el-icon-s-finance', name: '财务' },
        { value: 'el-icon-s-claim', name: '声明' },
        { value: 'el-icon-icon-set-up', name: '设置组' },
        { value: 'el-icon-s-shop', name: '商店' },
        { value: 'el-icon-menu', name: '列表' },
        { value: 'el-icon-setting', name: '设置' },
        { value: 'el-icon-location', name: '位置' },
        { value: 'el-icon-notebook-2', name: '笔记本' },
        { value: 'el-icon-document', name: '文档' },
        { value: 'el-icon-tickets', name: '票券' },
        { value: 'el-icon-date', name: '日期' },
        { value: 'el-icon-news', name: '消息' },
        { value: 'el-icon-message', name: '消息2' },
        { value: 'el-icon-bell', name: '铃铛' },
        { value: 'el-icon-phone', name: '电话' },
        { value: 'el-icon-lock', name: '锁' },
        { value: 'el-icon-view', name: '视图' },
        { value: 'el-icon-picture', name: '图片' },
        { value: 'el-icon-camera', name: '相机' },
        { value: 'el-icon-video-camera', name: '视频' },
        { value: 'el-icon-search', name: '搜索' },
        { value: 'el-icon-data-line', name: '数据线' },
        { value: 'el-icon-data-analysis', name: '数据分析' },
        { value: 'el-icon-data-board', name: '数据板' },
        { value: 'el-icon-pie-chart', name: '饼图' },
        { value: 'el-icon-upload', name: '上传' },
        { value: 'el-icon-download', name: '下载' },
        { value: 'el-icon-goods', name: '货物' },
        { value: 'el-icon-sold-out', name: '售罄' },
        { value: 'el-icon-shopping-cart-2', name: '购物车' },
        { value: 'el-icon-shopping-bag-2', name: '购物袋' },
        { value: 'el-icon-bank-card', name: '银行卡' },
        { value: 'el-icon-money', name: '钱' },
        { value: 'el-icon-wallet', name: '钱包' },
        { value: 'el-icon-bug', name: '虫' },
        { value: 'el-icon-sunny', name: '太阳' },
        { value: 'el-icon-cloudy', name: '多云' },
        { value: 'el-icon-warning', name: '警告' },
        { value: 'el-icon-warning-outline', name: '警告线' },
        { value: 'el-icon-s-opportunity', name: '机会' },
        { value: 'el-icon-map-location', name: '地图' },
        { value: 'el-icon-chat-dot-round', name: '聊天' },
      ]
    }
  },
  computed: {
    parentMenus() {
      // 筛选出可以作为父级的菜单（一级菜单，或排除自己）
      return this.allMenus.filter(m => !m.pid && (!this.form.id || m.id !== this.form.id))
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      this.request.get("/api/menu", {
        params: { name: this.name }
      }).then(res => {
        let menuData = []
        if (res && res.code === '200' && res.data) {
          menuData = res.data
        } else if (res && Array.isArray(res)) {
          menuData = res
        }

        this.tableData = menuData
        this.totalMenus = this.countTotal(menuData)
        this.parentCount = menuData.length
      }).catch(err => {
        console.error('获取菜单数据失败:', err)
        this.$message.error('获取菜单数据失败')
        this.tableData = []
      }).finally(() => {
        this.loading = false
      })

      // 同时获取平铺列表，用于上级菜单下拉选择
      this.request.get("/api/menu/flat").then(res => {
        if (res && res.code === '200' && res.data) {
          this.allMenus = res.data
        }
      }).catch(() => {
        this.allMenus = []
      })
    },

    countTotal(tree) {
      let count = 0
      const traverse = (arr) => {
        arr.forEach(item => {
          count++
          if (item.children && item.children.length) {
            traverse(item.children)
          }
        })
      }
      traverse(tree)
      return count
    },

    save() {
      this.$refs.menuForm.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          const method = this.form.id ? 'put' : 'post'
          const payload = { ...this.form }
          if (!payload.pid) payload.pid = null

          this.request[method]("/api/menu", payload).then(res => {
            if (res && (res.code === '200' || res === true)) {
              this.$message.success(this.form.id ? '更新成功' : '创建成功')
              this.dialogFormVisible = false
              this.load()
            } else {
              this.$message.error('保存失败：' + (res.message || res.msg || '未知错误'))
            }
          }).catch(err => {
            console.error('保存失败:', err)
            this.$message.error('保存失败：' + (err.message || '未知错误'))
          }).finally(() => {
            this.saveLoading = false
          })
        }
      })
    },

    handleAdd(pid) {
      this.form = {
        name: '',
        path: '',
        pagePath: '',
        icon: '',
        sortNum: 0,
        description: '',
        pid: pid || null
      }
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs.menuForm && this.$refs.menuForm.clearValidate()
      })
    },

    handleEdit(row) {
      this.form = {
        id: row.id,
        name: row.name,
        path: row.path || '',
        pagePath: row.pagePath || '',
        icon: row.icon || '',
        sortNum: row.sortNum || 0,
        description: row.description || '',
        pid: row.pid || null
      }
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs.menuForm && this.$refs.menuForm.clearValidate()
      })
    },

    del(id) {
      this.request.delete("/api/menu/" + id).then(res => {
        if (res && (res.code === '200' || res === true)) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败：" + (res.message || res.msg || '未知错误'))
        }
      }).catch(err => {
        console.error('删除失败:', err)
        this.$message.error("删除失败：" + (err.message || '未知错误'))
      })
    },

    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    delBatch() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要删除的菜单')
        return
      }

      const ids = this.multipleSelection.map(v => v.id)
      const promises = ids.map(id => this.request.delete("/api/menu/" + id))

      Promise.all(promises).then(responses => {
        const success = responses.filter(r => r && (r.code === '200' || r === true)).length
        if (success === ids.length) {
          this.$message.success(`批量删除成功，共删除 ${success} 条`)
        } else {
          this.$message.warning(`部分删除成功，成功 ${success}/${ids.length} 条`)
        }
        this.load()
      }).catch(err => {
        this.$message.error('批量删除失败：' + (err.message || '未知错误'))
      })
    },

    reset() {
      this.name = ""
      this.load()
    },

    getSortTagType(num) {
      if (num < 100) return 'success'
      if (num < 500) return 'primary'
      if (num < 1000) return 'warning'
      return 'info'
    },

    expandAll() {
      this.toggleAll(true)
    },

    collapseAll() {
      this.toggleAll(false)
    },

    toggleAll(expand) {
      const table = this.$el.querySelector('.menu-table')
      if (!table) return
      const rows = table.querySelectorAll('.el-table__row')
      rows.forEach(row => {
        const toggleBtn = row.querySelector('.el-table__expand-icon')
        if (toggleBtn) {
          const isExpanded = toggleBtn.classList.contains('el-table__expand-icon--expanded')
          if (expand && !isExpanded) {
            toggleBtn.click()
          } else if (!expand && isExpanded) {
            toggleBtn.click()
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.menu-management-container {
  padding: 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 50%, #f0fdfa 100%);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 24px 30px;
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #0d9488 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3);
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.header-content {
  position: relative;
  z-index: 1;
}

.page-title {
  margin: 0 0 6px 0;
  font-size: 26px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 28px;
}

.page-subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 36px;
  position: relative;
  z-index: 1;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-label {
  font-size: 12px;
  opacity: 0.85;
}

.action-card,
.table-card {
  margin-bottom: 20px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.action-card:hover,
.table-card:hover {
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.1);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-input {
  width: 280px;
}

.search-btn,
.reset-btn {
  border-radius: 10px;
  font-weight: 500;
}

.add-btn {
  background: linear-gradient(135deg, #10b981, #059669);
  border: none;
  border-radius: 10px;
  font-weight: 500;
  padding: 10px 20px;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.35);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  color: #10b981;
}

.table-actions {
  display: flex;
  gap: 4px;
}

.menu-table {
  border-radius: 10px;
  overflow: hidden;
}

.menu-table ::v-deep .el-table__header th {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%) !important;
  color: #065f46 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #10b981 !important;
}

.menu-table ::v-deep .el-table__row:hover > td {
  background: #f0fdf4 !important;
}

.menu-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-icon {
  font-size: 16px;
  color: #10b981;
}

.menu-name {
  font-weight: 500;
  color: #1f2937;
}

.leaf-tag,
.parent-tag {
  margin-left: 4px;
}

.path-text {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
}

.page-path-text {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  color: #059669;
  background: #ecfdf5;
  padding: 2px 8px;
  border-radius: 4px;
}

.no-path,
.no-icon,
.no-desc {
  color: #d1d5db;
  font-style: italic;
}

.table-icon {
  font-size: 18px;
  color: #10b981;
}

.row-actions {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.action-btn {
  transition: all 0.2s ease;
}

.action-btn:hover {
  transform: scale(1.15);
}

/* 对话框 */
.menu-dialog .el-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.menu-dialog .el-dialog__header {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  padding: 20px 24px !important;
  margin-right: 0 !important;
}

.menu-dialog .el-dialog__title {
  color: white !important;
  font-weight: 600;
  font-size: 18px;
}

.menu-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white !important;
  font-size: 20px;
}

.menu-dialog .el-dialog__body {
  padding: 24px !important;
}

.dialog-footer {
  padding: 16px 24px !important;
  border-top: 1px solid #e5e7eb;
  text-align: right;
}

/* 响应式 */
@media (max-width: 768px) {
  .menu-management-container {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 20px;
  }

  .page-title {
    font-size: 20px;
    justify-content: center;
  }

  .header-stats {
    gap: 24px;
  }

  .stat-number {
    font-size: 22px;
  }

  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .action-buttons {
    justify-content: center;
  }

  .menu-dialog {
    width: 95% !important;
  }
}
</style>
