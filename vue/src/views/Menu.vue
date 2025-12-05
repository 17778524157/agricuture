<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd(null)">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
              row-key="id" default-expand-all @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="path" label="路径"></el-table-column>
      <el-table-column prop="pagePath" label="页面路径"></el-table-column>
      <el-table-column label="图标" class-name="fontSize18" align="center" label-class-name="fontSize12">
        <template slot-scope="scope">
          <span :class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="sortNum" label="顺序"></el-table-column>
      <el-table-column label="操作"  width="300" align="center">
        <template slot-scope="scope">
          <el-button type="primary" @click="handleAdd(scope.row.id)" v-if="!scope.row.pid && !scope.row.path">新增子菜单 <i class="el-icon-plus"></i></el-button>
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="菜单信息" :visible.sync="dialogFormVisible" width="30%" >
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="路径">
          <el-input v-model="form.path" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="页面路径">
          <el-input v-model="form.pagePath" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-select clearable v-model="form.icon" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in options" :key="item.name" :label="item.name" :value="item.value">
              <i :class="item.value" /> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="顺序">
          <el-input v-model="form.sortNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
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
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      options: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/api/menu", {
        params: {
          name: this.name,
        }
      }).then(res => {
        console.log('菜单API返回数据:', res)
        
        // 处理微服务返回的Result格式
        let menuData = null
        if (res && res.code === '200' && res.data && Array.isArray(res.data)) {
          menuData = res.data
        } else if (res && Array.isArray(res)) {
          menuData = res
        } else if (res && res.data && Array.isArray(res.data)) {
          menuData = res.data
        }
        
        if (menuData && menuData.length > 0) {
          this.tableData = menuData
          this.$message.success(`成功获取${menuData.length}个菜单`)
        } else {
          this.tableData = []
          this.$message.info('暂无菜单数据')
        }
      }).catch(err => {
        console.error('获取菜单数据失败:', err)
        this.tableData = []
        this.$message.warning('获取菜单数据失败，请检查服务连接')
      })

      // 请求图标的数据（暂时使用备用数据）
      this.options = [
        { value: 'el-icon-s-home', label: '首页' },
        { value: 'el-icon-user', label: '用户' },
        { value: 'el-icon-s-custom', label: '角色' },
        { value: 'el-icon-menu', label: '菜单' },
        { value: 'el-icon-s-goods', label: '商品' },
        { value: 'el-icon-s-marketing', label: '营销' },
        { value: 'el-icon-shopping-cart-2', label: '购物车' },
        { value: 'el-icon-s-data', label: '数据' },
        { value: 'el-icon-s-tools', label: '工具' },
        { value: 'el-icon-setting', label: '设置' }
      ]
    },
    save() {
      // 根据是否有ID判断是新增还是编辑
      const apiUrl = "/api/menu"
      const method = this.form.id ? 'put' : 'post'
      
      console.log('保存菜单数据:', this.form)
      console.log('API方法:', method)
      
      this.request[method](apiUrl, this.form).then(res => {
        console.log('保存响应:', res)
        if (res && res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败：" + (res.message || '未知错误'))
        }
      }).catch(err => {
        console.error('保存菜单失败:', err)
        this.$message.error("保存失败：" + (err.message || '未知错误'))
      })
    },
    handleAdd(pid) {
      this.dialogFormVisible = true
      this.form = {}
      if (pid) {
        this.form.pid = pid
      }
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/api/menu/" + id).then(res => {
        console.log('删除响应:', res)
        if (res && res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败：" + (res.message || '未知错误'))
        }
      }).catch(err => {
        console.error('删除菜单失败:', err)
        this.$message.error("删除失败：" + (err.message || '未知错误'))
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      
      // 微服务暂时不支持批量删除，改为逐个删除
      if (ids.length === 0) {
        this.$message.warning('请选择要删除的菜单')
        return
      }
      
      let deleteCount = 0
      let totalCount = ids.length
      
      ids.forEach(id => {
        this.request.delete("/api/menu/" + id).then(res => {
          deleteCount++
          if (res && res.code === '200') {
            if (deleteCount === totalCount) {
              this.$message.success("批量删除成功")
              this.load()
            }
          } else {
            this.$message.error(`删除菜单ID ${id} 失败`)
          }
        }).catch(err => {
          deleteCount++
          this.$message.error(`删除菜单ID ${id} 失败：${err.message}`)
        })
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
    exp() {
      window.open("http://localhost:8080/api/role/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style>
.headerBg {
  background: #eee!important;
}
.fontSize18{
  font-size: 18px;
}
.fontSize12{
  font-size: 12px;
}
</style>
