<template>
  <div class="purchase-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">采购管理</h2>
      <p class="page-subtitle">管理采购订单和供应商信息</p>
    </div>

    <!-- 搜索和操作区域 -->
    <el-card class="operation-card" shadow="hover">
      <div class="search-section">
        <el-input
          v-model="product"
          placeholder="请输入产品名称搜索"
          class="search-input"
          prefix-icon="el-icon-search"
          clearable
          @keyup.enter.native="load"
        >
          <el-button slot="append" icon="el-icon-search" @click="load">搜索</el-button>
        </el-input>
        <el-button type="warning" icon="el-icon-refresh" @click="reset">重置</el-button>
      </div>

      <div class="operation-section">
        <el-button-group class="operation-group">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增采购</el-button>
          <el-popconfirm
            confirm-button-text='确定'
            cancel-button-text='取消'
            icon="el-icon-warning"
            icon-color="red"
            title="确定要批量删除选中的采购记录吗？"
            @confirm="delBatch"
          >
            <el-button type="danger" icon="el-icon-delete" slot="reference" :disabled="!multipleSelection.length">
              批量删除
            </el-button>
          </el-popconfirm>
        </el-button-group>

        <el-button-group class="operation-group">
          <el-upload
            action="http://localhost:8080/api/purchase/import"
            :show-file-list="false"
            accept=".xlsx"
            :on-success="handleExcelImportSuccess"
            :before-upload="beforeExcelUpload"
          >
            <el-button type="success" icon="el-icon-upload2">导入数据</el-button>
          </el-upload>
          <el-button type="info" icon="el-icon-download" @click="exp">导出数据</el-button>
        </el-button-group>
      </div>
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        element-loading-text="加载中..."
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(255, 255, 255, 0.8)"
        :header-cell-class-name="'table-header'"
        @selection-change="handleSelectionChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" sortable align="center"></el-table-column>
        <el-table-column prop="product" label="产品" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price" label="价格（元）" width="120" align="right">
          <template slot-scope="scope">
            <span class="price-cell">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="number" label="采购数量" width="120" align="center"></el-table-column>
        <el-table-column prop="provider" label="供应商" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column prop="phone" label="电话" width="140" align="center"></el-table-column>
        <el-table-column prop="purchaser" label="采购人" width="120" align="center"></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip></el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button-group>
              <el-button
                type="primary"
                icon="el-icon-edit"
                size="mini"
                @click="handleEdit(scope.row)"
                title="编辑"
              ></el-button>
              <el-popconfirm
                confirm-button-text='确定'
                cancel-button-text='取消'
                icon="el-icon-warning"
                icon-color="red"
                title="确定要删除这条采购记录吗？"
                @confirm="del(scope.row.id)"
              >
                <el-button
                  type="danger"
                  icon="el-icon-delete"
                  size="mini"
                  slot="reference"
                  title="删除"
                ></el-button>
              </el-popconfirm>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
        >
        </el-pagination>
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      :title="form.id ? '编辑采购信息' : '新增采购信息'"
      :visible.sync="dialogFormVisible"
      width="600px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="purchase-dialog"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="form"
        label-width="100px"
        size="small"
        class="purchase-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品" prop="product">
              <el-input v-model="form.product" placeholder="请输入产品名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="form.price"
                :precision="2"
                :step="0.1"
                :min="0"
                controls-position="right"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购数量" prop="number">
              <el-input-number
                v-model="form.number"
                :min="1"
                controls-position="right"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="provider">
              <el-input v-model="form.provider" placeholder="请输入供应商名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址"></el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购人" prop="purchaser">
              <el-input v-model="form.purchaser" placeholder="请输入采购人姓名"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            type="textarea"
            v-model="form.remark"
            :rows="3"
            placeholder="请输入备注信息"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save" :loading="saveLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Purchase",
  data() {
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
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      product: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      loading: false,
      saveLoading: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      rules: {
        product: [
          { required: true, message: '请输入产品名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' },
          { type: 'number', message: '价格必须为数字', trigger: 'blur' }
        ],
        number: [
          { required: true, message: '请输入采购数量', trigger: 'blur' },
          { type: 'number', message: '数量必须为数字', trigger: 'blur' }
        ],
        provider: [
          { required: true, message: '请输入供应商名称', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        purchaser: [
          { required: true, message: '请输入采购人姓名', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true;
      this.request.get("/purchase/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          product: this.product,
        }
      }).then(res => {
        this.tableData = res.data.records;
        this.total = res.data.total;
      }).finally(() => {
        this.loading = false;
      });
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.saveLoading = true;
          this.request.post("/purchase", this.form).then(res => {
            if (res.code === '200') {
              this.$message({
                message: this.form.id ? "更新成功" : "添加成功",
                type: "success",
                duration: 2000
              });
              this.dialogFormVisible = false;
              this.load();
            } else {
              this.$message.error(res.msg || "操作失败");
            }
          }).finally(() => {
            this.saveLoading = false;
          });
        }
      });
    },
    handleAdd() {
      this.form = {};
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate();
      });
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate();
      });
    },
    del(id) {
      this.request.delete("/purchase/" + id).then(res => {
        if (res.code === '200') {
          this.$message({
            message: "删除成功",
            type: "success",
            duration: 2000
          });
          this.load();
        } else {
          this.$message.error(res.msg || "删除失败");
        }
      });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.warning("请选择需要删除的数据");
        return;
      }
      const ids = this.multipleSelection.map(v => v.id);
      this.request.post("/purchase/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message({
            message: "批量删除成功",
            type: "success",
            duration: 2000
          });
          this.load();
        } else {
          this.$message.error(res.msg || "批量删除失败");
        }
      });
    },
    reset() {
      this.product = "";
      this.load();
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.load();
    },
    exp() {
      window.open("http://localhost:8080/api/purchase/export");
    },
    handleExcelImportSuccess() {
      this.$message({
        message: "数据导入成功",
        type: "success",
        duration: 2000
      });
      this.load();
    },
    beforeExcelUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isExcel) {
        this.$message.error('只能上传 Excel 文件!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('文件大小不能超过 2MB!');
        return false;
      }
      return true;
    }
  }
}
</script>

<style scoped>
.purchase-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  color: #303133;
  margin: 0;
  font-weight: 600;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
  margin: 10px 0 0;
}

.operation-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.operation-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.operation-group {
  display: flex;
  gap: 10px;
}

.table-card {
  border-radius: 8px;
}

.data-table {
  width: 100%;
  margin-bottom: 20px;
}

.table-header {
  background-color: #f5f7fa !important;
  color: #606266;
  font-weight: 600;
}

.price-cell {
  color: #f56c6c;
  font-weight: 500;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.purchase-dialog {
  border-radius: 8px;
}

.purchase-form {
  padding: 20px;
}

.dialog-footer {
  text-align: right;
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .purchase-container {
    padding: 10px;
  }

  .search-section {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .operation-section {
    flex-direction: column;
    align-items: stretch;
  }

  .operation-group {
    width: 100%;
    justify-content: space-between;
  }

  .el-button-group {
    display: flex;
    width: 100%;
  }

  .el-button-group .el-button {
    flex: 1;
  }
}

/* 动画效果 */
.el-button {
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.el-card {
  transition: all 0.3s ease;
}

.el-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 表格行悬浮效果 */
.data-table .el-table__row {
  transition: all 0.3s ease;
}

.data-table .el-table__row:hover {
  background-color: #f5f7fa !important;
  transform: scale(1.01);
}

/* 输入框焦点效果 */
.el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64,158,255,.2);
}

/* 按钮组样式优化 */
.el-button-group .el-button {
  margin: 0;
}

.el-button-group .el-button:not(:last-child) {
  margin-right: -1px;
}

/* 分页器样式优化 */
.el-pagination {
  padding: 20px 0;
  text-align: center;
}

.el-pagination .el-pagination__total,
.el-pagination .el-pagination__jump {
  margin-right: 16px;
}

.el-pagination .el-pagination__sizes {
  margin-right: 16px;
}

/* 对话框样式优化 */
.el-dialog__header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.el-dialog__body {
  padding: 30px 20px;
}

/* 表单样式优化 */
.el-form-item__label {
  font-weight: 500;
}

.el-input-number .el-input__inner {
  text-align: left;
}

/* 表格内容过长显示省略号 */
.el-table .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
