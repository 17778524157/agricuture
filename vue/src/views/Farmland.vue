<template>
  <div class="farmland-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>农田管理</h2>
      <p class="subtitle">管理系统农田信息，支持发布、编辑和删除操作</p>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <i class="el-icon-s-home stat-icon"></i>
            <div class="stat-info">
              <div class="stat-title">农田总数</div>
              <div class="stat-value">{{ statistics.totalFarms }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <i class="el-icon-s-goods stat-icon"></i>
            <div class="stat-info">
              <div class="stat-title">作物种类</div>
              <div class="stat-value">{{ statistics.cropTypes }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <i class="el-icon-s-data stat-icon"></i>
            <div class="stat-info">
              <div class="stat-title">总种植面积</div>
              <div class="stat-value">{{ statistics.totalArea }}亩</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <i class="el-icon-user stat-icon"></i>
            <div class="stat-info">
              <div class="stat-title">负责人数</div>
              <div class="stat-value">{{ statistics.keeperCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和操作区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input
            v-model="farm"
            placeholder="请输入农田名称"
            prefix-icon="el-icon-search"
            clearable
            class="search-input"
            @keyup.enter.native="load"
          >
            <el-button slot="append" icon="el-icon-search" @click="load">搜索</el-button>
          </el-input>
          <el-select v-model="cropFilter" placeholder="作物类型" clearable class="filter-select">
            <el-option
              v-for="item in cropOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </div>
        <div class="button-group">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增农田</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="reset">重置</el-button>
          <el-popconfirm
            confirm-button-text="确定"
            cancel-button-text="取消"
            icon="el-icon-warning"
            icon-color="red"
            title="确定要批量删除选中的农田吗？"
            @confirm="delBatch"
          >
            <el-button type="danger" icon="el-icon-delete" slot="reference" :disabled="!multipleSelection.length">
              批量删除
            </el-button>
          </el-popconfirm>
          <el-button type="success" icon="el-icon-download" @click="exp">导出数据</el-button>
        </div>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        :header-cell-class-name="'headerBg'"
        @selection-change="handleSelectionChange"
        class="farmland-table"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" sortable align="center"></el-table-column>
        <el-table-column prop="farm" label="农田名称" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="crop" label="作物名称" min-width="120" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-tag :type="getCropTagType(scope.row.crop)">{{ scope.row.crop }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="area" label="农田面积（亩）" width="120" align="center" sortable>
          <template slot-scope="scope">
            <span class="area-value">{{ scope.row.area }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="农田地址" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-button type="text" @click="showMap(scope.row)">{{ scope.row.address }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="keeper" label="农田负责人" width="120" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="success">{{ scope.row.keeper }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plantDate" label="种植时间" width="120" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="info">{{ scope.row.plantDate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="harvestDate" label="预计收获" width="120" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="warning">{{ scope.row.harvestDate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-edit"
              @click="handleEdit(scope.row)"
              circle
            ></el-button>
            <el-popconfirm
              confirm-button-text="确定"
              cancel-button-text="取消"
              icon="el-icon-warning"
              icon-color="red"
              title="确定要删除这条农田信息吗？"
              @confirm="del(scope.row.id)"
            >
              <el-button
                type="danger"
                size="mini"
                icon="el-icon-delete"
                slot="reference"
                circle
              ></el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      :title="form.id ? '编辑农田信息' : '新增农田信息'" 
      :visible.sync="dialogFormVisible" 
      width="50%" 
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="120px"
        size="small"
        class="farmland-form"
      >
        <el-form-item label="农田名称" prop="farm">
          <el-input v-model="form.farm" placeholder="请输入农田名称" maxlength="50" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="作物名称" prop="crop">
          <el-select v-model="form.crop" placeholder="请选择作物类型" style="width: 100%">
            <el-option
              v-for="item in cropOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="农田面积（亩）" prop="area">
          <el-input-number 
            v-model="form.area" 
            :min="0" 
            :max="10000" 
            :precision="2" 
            :step="0.1"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="农田地址" prop="address">
          <el-input 
            v-model="form.address" 
            placeholder="请输入农田地址" 
            maxlength="200" 
            show-word-limit
            readonly
            @click="showMapPicker"
          >
            <el-button slot="append" icon="el-icon-location" @click="showMapPicker">选择位置</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="农田负责人" prop="keeper">
          <el-input 
            v-model="form.keeper" 
            placeholder="请输入负责人姓名" 
            maxlength="20" 
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="种植时间" prop="plantDate">
          <el-date-picker
            v-model="form.plantDate"
            type="date"
            placeholder="选择种植时间"
            style="width: 100%"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="预计收获时间" prop="harvestDate">
          <el-date-picker
            v-model="form.harvestDate"
            type="date"
            placeholder="选择预计收获时间"
            style="width: 100%"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="备注说明" prop="remark">
          <el-input 
            type="textarea" 
            v-model="form.remark" 
            placeholder="请输入备注说明" 
            :rows="3"
            maxlength="500" 
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save" :loading="saveLoading">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 地图预览对话框 -->
    <el-dialog
      title="农田位置预览"
      :visible.sync="mapDialogVisible"
      width="70%"
      :close-on-click-modal="false"
    >
      <div class="map-container" ref="mapContainer"></div>
    </el-dialog>

    <!-- 地图选点组件 -->
    <MapPicker
      :visible.sync="mapPickerVisible"
      :initial-location="initialMapLocation"
      @location-selected="handleLocationSelected"
    />
  </div>
</template>

<script>
import MapPicker from '@/components/MapPicker.vue'

export default {
  name: "Farmland",
  components: {
    MapPicker
  },
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      farm: "",
      cropFilter: "",
      form: {},
      dialogFormVisible: false,
      mapDialogVisible: false,
      mapPickerVisible: false,
      initialMapLocation: {
        latitude: 39.9042,
        longitude: 116.4074,
        address: '北京市'
      },
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      loading: false,
      saveLoading: false,
      statistics: {
        totalFarms: 0,
        cropTypes: 0,
        totalArea: 0,
        keeperCount: 0
      },
      cropOptions: [
        { value: '酸枣', label: '酸枣' },
        { value: '其他', label: '其他' }
      ],
      rules: {
        farm: [
          { required: true, message: '请输入农田名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        crop: [
          { required: true, message: '请选择作物类型', trigger: 'change' }
        ],
        area: [
          { required: true, message: '请输入农田面积', trigger: 'blur' },
          { type: 'number', message: '面积必须为数字', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入农田地址', trigger: 'blur' },
          { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
        ],
        keeper: [
          { required: true, message: '请输入负责人姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        plantDate: [
          { required: true, message: '请选择种植时间', trigger: 'change' }
        ],
        harvestDate: [
          { required: true, message: '请选择预计收获时间', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.load()
    this.loadStatistics()
  },
  methods: {
    load() {
      this.loading = true
      console.log('尝试加载农田数据')
      
      // 先尝试使用微服务API，如果失败则使用模拟数据
      this.request.get("/api/field").then(res => {
        console.log('微服务API返回数据:', res)
        console.log('响应类型:', typeof res)
        console.log('响应结构:', Object.keys(res || {}))
        
        // 处理微服务返回的Result格式: {code: "200", message: "操作成功", data: [...]}
        let fieldsData = null
        if (res && res.code === '200' && res.data && Array.isArray(res.data)) {
          // 微服务成功返回数据
          fieldsData = res.data
        } else if (res && res.data && Array.isArray(res.data)) {
          // 兼容其他格式
          fieldsData = res.data
        } else if (res && Array.isArray(res)) {
          // 直接返回数组
          fieldsData = res
        } else if (res && res.data && typeof res.data === 'object' && !Array.isArray(res.data)) {
          // 如果是单个对象，转换为数组
          fieldsData = [res.data]
        }
        
        if (fieldsData && fieldsData.length > 0) {
          // 转换数据格式以适配表格展示
          this.tableData = fieldsData.map(field => ({
            id: field.id,
            farm: field.name,
            area: field.area || 0,
            address: field.description || '未设置地址',
            keeper: field.manager || '未设置负责人',
            crop: field.crop || '未设置作物',
            state: field.status || '未知状态',
            plantDate: field.plantDate ? this.formatDate(new Date(field.plantDate)) : '未设置',
            harvestDate: field.harvestDate ? this.formatDate(new Date(field.harvestDate)) : '未设置',
            latitude: field.latitude || 39.9042,
            longitude: field.longitude || 116.4074
          }))
          this.total = fieldsData.length
          this.$message.success(`成功获取${fieldsData.length}条农田数据`)
        } else {
          // 如果没有数据，显示空状态
          this.tableData = []
          this.total = 0
          this.$message.info('暂无农田数据')
        }
      }).catch(err => {
        console.error('获取农田数据失败:', err)
        console.error('错误详情:', {
          message: err.message,
          response: err.response,
          status: err.response?.status,
          data: err.response?.data
        })
        
        // 使用模拟数据作为备用方案
        console.log('使用模拟数据作为备用方案')
        const mockData = [
          { id: 1, name: '1号农田', area: 100, description: '优质水稻种植区', manager: '张三三', crop: '水稻', status: '种植中', latitude: 39.9042, longitude: 116.4074, plantDate: '2024-03-15', harvestDate: '2024-09-15' },
          { id: 2, name: '2号农田', area: 150, description: '冬小麦种植区', manager: '张三三', crop: '小麦', status: '种植中', latitude: 39.9142, longitude: 116.4174, plantDate: '2024-02-20', harvestDate: '2024-07-20' },
          { id: 3, name: '3号农田', area: 200, description: '玉米种植示范区', manager: '张三三', crop: '玉米', status: '种植中', latitude: 39.8942, longitude: 116.3974, plantDate: '2024-04-01', harvestDate: '2024-10-01' }
        ]
        
        // 转换模拟数据格式
        this.tableData = mockData.map(field => ({
          id: field.id,
          farm: field.name,
          area: field.area || 0,
          address: field.description || '未设置地址',
          keeper: field.manager || '未设置负责人',
          crop: field.crop || '未设置作物',
          state: field.status || '未知状态',
          plantDate: field.plantDate ? this.formatDate(new Date(field.plantDate)) : '未设置',
          harvestDate: field.harvestDate ? this.formatDate(new Date(field.harvestDate)) : '未设置',
          latitude: field.latitude || 39.9042,
          longitude: field.longitude || 116.4074
        }))
        this.total = mockData.length
        
        // 根据错误类型显示不同的消息
        if (err.response?.status === 401) {
          this.$message.warning('未授权访问，使用模拟数据演示')
        } else if (err.response?.status === 404) {
          this.$message.warning('API接口不存在，使用模拟数据演示')
        } else if (err.response?.status === 500) {
          this.$message.warning('服务器内部错误，使用模拟数据演示')
        } else if (err.code === 'ECONNREFUSED') {
          this.$message.warning('无法连接到服务器，使用模拟数据演示')
        } else {
          this.$message.warning('获取农田数据失败，使用模拟数据演示: ' + (err.message || '网络错误'))
        }
      }).finally(() => {
        this.loading = false
        this.loadStatistics() // 确保统计信息被更新
      })
    },
    loadStatistics() {
      // 从农田数据计算统计信息
      const totalArea = this.tableData.reduce((sum, item) => sum + (item.area || 0), 0)
      const cropTypes = new Set(this.tableData.map(item => item.crop)).size
      const keeperCount = new Set(this.tableData.map(item => item.keeper)).size
      
      this.statistics = {
        totalFarms: this.total || 0,
        cropTypes: cropTypes,
        totalArea: totalArea.toFixed(1),
        keeperCount: keeperCount
      }
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          
          // 转换表单数据为微服务API格式
          const fieldData = {
            name: this.form.farm,
            area: this.form.area,
            description: this.form.address,
            manager: this.form.keeper,
            crop: this.form.crop,
            status: '种植中',
            plantDate: this.form.plantDate,
            harvestDate: this.form.harvestDate,
            latitude: this.form.latitude,
            longitude: this.form.longitude
          }
          
          // 根据是否有ID判断是新增还是编辑
          const apiUrl = "/api/farm/fields"
          const method = this.form.id ? 'put' : 'post'
          
          if (this.form.id) {
            fieldData.id = this.form.id
          }
          
          this.request[method](apiUrl, fieldData).then(res => {
            console.log('保存响应:', res)
            if (res && (res.code === '200' || res === true || res.data === true)) {
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.load()
              this.loadStatistics()
            } else {
              this.$message.error(res.msg || "保存失败")
            }
          }).catch(err => {
            console.error('保存错误:', err)
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
    handleAdd() {
      this.dialogFormVisible = true
      // 设置默认值
      const today = new Date()
      const harvestDate = new Date()
      harvestDate.setMonth(today.getMonth() + 6) // 6个月后收获
      
      this.form = {
        area: 0,
        plantDate: this.formatDate(today),
        harvestDate: this.formatDate(harvestDate),
        latitude: 39.9042,
        longitude: 116.4074,
        address: ''
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      // 将表格数据转换为表单数据格式
      this.form = {
        id: row.id,
        farm: row.farm,
        crop: row.crop,
        area: row.area,
        address: row.address,
        keeper: row.keeper,
        plantDate: row.plantDate || this.formatDate(new Date()),
        harvestDate: row.harvestDate || this.formatDate(new Date(Date.now() + 6 * 30 * 24 * 60 * 60 * 1000)),
        latitude: row.latitude || 39.9042,
        longitude: row.longitude || 116.4074
      }
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleDialogClose() {
      this.$refs.form && this.$refs.form.resetFields()
      this.form = {}
    },
    del(id) {
      this.request.delete("/api/farm/fields/" + id).then(res => {
        if (res && (res.code === '200' || res === true)) {
          this.$message.success("删除成功")
          this.load()
          this.loadStatistics()
        } else {
          this.$message.error("删除失败")
        }
      }).catch(err => {
        this.$message.error("删除失败：" + (err.message || '未知错误'))
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      
      // 由于微服务可能不支持批量删除，我们逐个删除
      const deletePromises = this.multipleSelection.map(item => 
        this.request.delete("/api/farm/fields/" + item.id)
      )
      
      Promise.all(deletePromises).then(responses => {
        const successCount = responses.filter(res => res && (res.code === '200' || res === true)).length
        if (successCount === this.multipleSelection.length) {
          this.$message.success(`批量删除成功，共删除 ${successCount} 条记录`)
        } else {
          this.$message.warning(`部分删除成功，成功删除 ${successCount} 条记录`)
        }
        this.load()
        this.loadStatistics()
      }).catch(err => {
        this.$message.error("批量删除失败：" + (err.message || '未知错误'))
      })
    },
    reset() {
      this.farm = ""
      this.cropFilter = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
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
      this.downloadCSV(csvContent, '农田数据.csv')
      this.$message.success('数据导出成功')
    },
    
    generateCSV(data) {
      const headers = ['ID', '农田名称', '作物名称', '农田面积(亩)', '农田地址', '负责人']
      const csvRows = [headers.join(',')]
      
      data.forEach(row => {
        const values = [
          row.id,
          `"${row.farm}"`,
          `"${row.crop}"`,
          row.area,
          `"${row.address}"`,
          `"${row.keeper}"`
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
    getCropTagType(crop) {
      const types = {
        '酸枣': 'danger',
        '其他': 'info'
      }
      return types[crop] || ''
    },
    showMap(row) {
      this.mapDialogVisible = true
      this.$nextTick(() => {
        // 使用Leaflet显示地图
        if (window.L) {
          const map = L.map(this.$refs.mapContainer).setView([row.latitude, row.longitude], 15)
          
          L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
          }).addTo(map)
          
          const marker = L.marker([row.latitude, row.longitude]).addTo(map)
          marker.bindPopup(`<b>${row.farm}</b><br/>${row.address}`).openPopup()
        } else {
          this.$message.warning('地图组件未加载')
        }
      })
    },
    showMapPicker() {
      // 设置初始位置
      if (this.form.latitude && this.form.longitude) {
        this.initialMapLocation = {
          latitude: this.form.latitude,
          longitude: this.form.longitude,
          address: this.form.address || '当前位置'
        }
      }
      this.mapPickerVisible = true
    },

    handleLocationSelected(location) {
      // 处理地图选点结果
      this.form.address = location.address
      this.form.latitude = location.latitude
      this.form.longitude = location.longitude
      this.mapPickerVisible = false
      this.$message.success('位置选择成功')
    },
    
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style lang="scss" scoped>
.farmland-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 60px);

  .page-header {
    margin-bottom: 30px;
    text-align: center;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    
    h2 {
      font-size: 28px;
      color: #303133;
      margin: 0 0 10px;
      font-weight: 600;
      background: linear-gradient(45deg, #409EFF, #67C23A);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    
    .subtitle {
      color: #606266;
      font-size: 15px;
      margin: 0;
      opacity: 0.8;
    }
  }

  .stat-cards {
    margin-bottom: 25px;

    .stat-card {
      border-radius: 12px;
      overflow: hidden;
      border: none;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
        
        .stat-icon {
          transform: scale(1.1);
          color: #67C23A;
        }
        
        .stat-value {
          color: #409EFF;
        }
      }

      .stat-item {
        display: flex;
        align-items: center;
        padding: 20px;
        position: relative;
        overflow: hidden;

        &::after {
          content: '';
          position: absolute;
          top: 0;
          right: 0;
          width: 100px;
          height: 100px;
          background: linear-gradient(45deg, transparent, rgba(64, 158, 255, 0.1));
          border-radius: 50%;
          transform: translate(30%, -30%);
        }

        .stat-icon {
          font-size: 48px;
          margin-right: 20px;
          color: #409EFF;
          transition: all 0.3s ease;
        }

        .stat-info {
          flex: 1;
          
          .stat-title {
            font-size: 14px;
            color: #909399;
            margin-bottom: 8px;
            font-weight: 500;
          }

          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #303133;
            transition: all 0.3s ease;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          }
        }
      }
    }
  }

  .search-card {
    margin-bottom: 25px;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    
    .search-wrapper {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 15px;
      padding: 5px;

      .search-inputs {
        display: flex;
        gap: 15px;
        flex: 1;

        .search-input {
          width: 300px;
          
          :deep(.el-input__inner) {
            border-radius: 20px;
            padding-left: 20px;
            transition: all 0.3s ease;
            
            &:focus {
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
            }
          }
        }

        .filter-select {
          width: 200px;
          
          :deep(.el-input__inner) {
            border-radius: 20px;
            padding-left: 20px;
          }
        }
      }

      .button-group {
        display: flex;
        gap: 12px;
        
        .el-button {
          border-radius: 20px;
          padding: 10px 20px;
          font-weight: 500;
          
          &.el-button--primary {
            background: linear-gradient(45deg, #409EFF, #67C23A);
            border: none;
            
            &:hover {
              background: linear-gradient(45deg, #67C23A, #409EFF);
            }
          }
        }
      }
    }
  }

  .table-card {
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    
    .farmland-table {
      margin: 10px 0;
      border-radius: 8px;
      overflow: hidden;

      :deep(.el-table__header-wrapper) {
        th {
          background: linear-gradient(to right, #f5f7fa, #e4e7ed) !important;
          color: #303133;
          font-weight: 600;
          height: 50px;
        }
      }

      :deep(.el-table__body-wrapper) {
        td {
          padding: 15px 0;
          transition: all 0.3s ease;
        }
        
        tr:hover > td {
          background-color: rgba(64, 158, 255, 0.1) !important;
        }
      }

      .area-value {
        font-weight: bold;
        color: #409EFF;
        font-size: 15px;
      }
      
      .el-tag {
        border-radius: 15px;
        padding: 0 15px;
        height: 28px;
        line-height: 26px;
        font-weight: 500;
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.05);
        }
      }
      
      .el-button--text {
        color: #409EFF;
        font-weight: 500;
        
        &:hover {
          color: #67C23A;
          text-decoration: underline;
        }
      }
    }
  }

  .pagination-container {
    margin-top: 25px;
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
    
    :deep(.el-pagination) {
      .el-pagination__total,
      .el-pagination__jump {
        margin-right: 15px;
      }
      
      .btn-prev,
      .btn-next,
      .el-pager li {
        border-radius: 20px;
        margin: 0 3px;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
        }
      }
      
      .el-pager li.active {
        background: linear-gradient(45deg, #409EFF, #67C23A);
        border: none;
      }
    }
  }

  .map-container {
    height: 500px;
    background: #f5f7fa;
    border-radius: 12px;
    box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1);
  }
}

// 对话框样式优化
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
  
  .el-dialog__header {
    background: linear-gradient(to right, #f5f7fa, #e4e7ed);
    padding: 20px;
    margin: 0;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .el-dialog__body {
    padding: 30px;
  }
  
  .el-dialog__footer {
    padding: 20px;
    border-top: 1px solid #ebeef5;
  }
  
  .farmland-form {
    .el-form-item {
      margin-bottom: 25px;
      
      .el-form-item__label {
        font-weight: 500;
        color: #606266;
      }
      
      .el-input__inner,
      .el-textarea__inner {
        border-radius: 8px;
        transition: all 0.3s ease;
        
        &:focus {
          box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
        }
      }
      
      .el-input-number {
        width: 100%;
        
        .el-input__inner {
          text-align: left;
        }
      }
    }
  }
}

// 响应式优化
@media screen and (max-width: 768px) {
  .farmland-container {
    padding: 15px;

    .page-header {
      padding: 15px;
      margin-bottom: 20px;
      
      h2 {
        font-size: 24px;
      }
    }

    .search-wrapper {
      flex-direction: column;
      align-items: stretch !important;

      .search-inputs {
        flex-direction: column;
        width: 100%;

        .search-input,
        .filter-select {
          width: 100% !important;
        }
      }

      .button-group {
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 8px;
        
        .el-button {
          flex: 1;
          min-width: 120px;
        }
      }
    }

    .stat-cards {
      .el-col {
        margin-bottom: 15px;
      }
    }
    
    .table-card {
      .farmland-table {
        :deep(.el-table__body-wrapper) {
          overflow-x: auto;
        }
      }
    }
  }
}

// 动画效果增强
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.farmland-container {
  animation: fadeIn 0.5s ease-out;
  
  .stat-card,
  .search-card,
  .table-card {
    animation: fadeIn 0.5s ease-out;
  }
}

// 滚动条美化
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
  
  &:hover {
    background: #909399;
  }
}
</style>
