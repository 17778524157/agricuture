<template>
  <div class="notice-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <i class="el-icon-bell title-icon"></i>
          <h2>公告管理</h2>
        </div>
        <p class="subtitle">管理系统公告信息，支持发布、编辑和删除操作</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <div class="stat-number">{{ total }}</div>
          <div class="stat-label">总公告数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ tableData.length }}</div>
          <div class="stat-label">当前页</div>
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="notice-tabs">
      <el-tab-pane label="公告列表" name="notice">
        <span slot="label">
          <i class="el-icon-document"></i>
          公告列表
        </span>
      </el-tab-pane>
      <el-tab-pane label="延迟发布任务" name="delayed">
        <span slot="label">
          <i class="el-icon-alarm-clock"></i>
          延迟发布任务
        </span>
      </el-tab-pane>
    </el-tabs>

    <!-- 搜索和操作区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <div class="search-section">
          <el-input
            v-model="name"
            placeholder="请输入公告标题进行搜索..."
            prefix-icon="el-icon-search"
            clearable
            class="search-input"
            @keyup.enter.native="load"
          >
            <el-button slot="append" icon="el-icon-search" @click="load">搜索</el-button>
          </el-input>
        </div>
        <div class="button-group">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd" class="action-btn primary">
            <i class="el-icon-plus"></i>
            <span>新增公告</span>
          </el-button>
          <el-button type="success" icon="el-icon-alarm-clock" @click="handleScheduleAdd" class="action-btn success">
            <i class="el-icon-alarm-clock"></i>
            <span>定时发布</span>
          </el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="reset" class="action-btn warning">
            <i class="el-icon-refresh"></i>
            <span>重置</span>
          </el-button>
          <el-popconfirm
            confirm-button-text="确定"
            cancel-button-text="取消"
            icon="el-icon-warning"
            icon-color="red"
            title="确定要批量删除选中的公告吗？"
            @confirm="delBatch"
          >
            <el-button type="danger" icon="el-icon-delete" slot="reference" :disabled="!multipleSelection.length" class="action-btn danger">
              <i class="el-icon-delete"></i>
              <span>批量删除</span>
            </el-button>
          </el-popconfirm>
        </div>
      </div>
    </el-card>

    <!-- 公告列表表格 -->
    <el-card class="table-card" shadow="hover" v-show="activeTab === 'notice'">
      <div class="table-header">
        <h3 class="table-title">
          <i class="el-icon-document"></i>
          公告列表
        </h3>
        <div class="table-actions">
          <el-tooltip content="刷新数据" placement="top">
            <el-button icon="el-icon-refresh" circle @click="load" :loading="loading"></el-button>
          </el-tooltip>
        </div>
      </div>
      
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        :header-cell-class-name="'headerBg'"
        @selection-change="handleSelectionChange"
        class="notice-table"
        :row-class-name="tableRowClassName"
      >
        <el-table-column type="selection" width="55" align="center">
          <template slot="header">
            <i class="el-icon-check"></i>
          </template>
        </el-table-column>
        
        <el-table-column prop="id" label="ID" width="80" sortable align="center">
          <template slot-scope="scope">
            <el-tag size="mini" type="info" effect="plain">{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="name" label="标题" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="title-cell">
              <i class="el-icon-document-copy"></i>
              <span class="title-text">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="content-cell">
              <span class="content-text">{{ scope.row.content }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="time" label="发布时间" width="160" align="center">
          <template slot-scope="scope">
            <div class="time-cell">
              <i class="el-icon-time"></i>
              <el-tag size="small" type="info" effect="light">{{ scope.row.time }}</el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="user" label="发布人" width="120" align="center">
          <template slot-scope="scope">
            <div class="user-cell">
              <i class="el-icon-user"></i>
              <el-tag size="small" type="success" effect="light">{{ scope.row.user }}</el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="图片" width="120" align="center">
          <template slot-scope="scope">
            <div class="image-cell">
              <el-image
                v-if="scope.row.img"
                style="width: 60px; height: 60px"
                :src="formatImageUrl(scope.row.img)"
                :preview-src-list="[formatImageUrl(scope.row.img)]"
                fit="cover"
                class="notice-image"
                @error="handleImageError(scope.row.img, $event)"
                @load="handleImageLoad(scope.row.img, $event)"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div v-else class="no-image">
                <i class="el-icon-picture-outline"></i>
                <span>暂无图片</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-tooltip content="查看延迟发布详情" placement="top">
                <el-button
                  type="info"
                  size="mini"
                  icon="el-icon-view"
                  @click="viewDelayedDetail(scope.row.id)"
                  circle
                  class="action-btn detail-btn"
                ></el-button>
              </el-tooltip>
              <el-tooltip content="编辑" placement="top">
                <el-button
                  type="primary"
                  size="mini"
                  icon="el-icon-edit"
                  @click="handleEdit(scope.row)"
                  circle
                  class="action-btn edit-btn"
                ></el-button>
              </el-tooltip>
              <el-popconfirm
                confirm-button-text="确定"
                cancel-button-text="取消"
                icon="el-icon-warning"
                icon-color="red"
                title="确定要删除这条公告吗？"
                @confirm="del(scope.row.id)"
              >
                <el-tooltip content="删除" placement="top">
                  <el-button
                    type="danger"
                    size="mini"
                    icon="el-icon-delete"
                    slot="reference"
                    circle
                    class="action-btn delete-btn"
                  ></el-button>
                </el-tooltip>
              </el-popconfirm>
            </div>
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

    <!-- 延迟发布任务列表表格 -->
    <el-card class="table-card" shadow="hover" v-show="activeTab === 'delayed'">
      <div class="table-header">
        <h3 class="table-title">
          <i class="el-icon-alarm-clock"></i>
          延迟发布任务列表
        </h3>
        <div class="table-actions">
          <el-button type="success" icon="el-icon-alarm-clock" @click="handleScheduleAdd" size="small">
            <i class="el-icon-alarm-clock"></i>
            新建定时任务
          </el-button>
        </div>
      </div>
      
      <!-- 搜索和筛选 -->
      <div class="search-wrapper" style="margin-bottom: 16px;">
        <el-input
          v-model="delayedTaskSearchTitle"
          placeholder="搜索任务标题..."
          prefix-icon="el-icon-search"
          clearable
          style="width: 300px; margin-right: 12px;"
          @keyup.enter.native="loadDelayedTasks"
        ></el-input>
        <el-select v-model="delayedTaskStatusFilter" placeholder="状态筛选" clearable style="width: 150px; margin-right: 12px;" @change="loadDelayedTasks">
          <el-option label="待发布" :value="0"></el-option>
          <el-option label="已发布" :value="1"></el-option>
          <el-option label="已取消" :value="2"></el-option>
          <el-option label="发布失败" :value="3"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="loadDelayedTasks">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetDelayedTaskSearch">重置</el-button>
      </div>

      <el-table
        v-loading="delayedTaskLoading"
        :data="delayedTaskData"
        border
        stripe
        class="notice-table"
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="id" label="任务ID" width="80" sortable align="center">
        </el-table-column>
        <el-table-column prop="title" label="公告标题" min-width="150" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="content" label="公告内容" min-width="200" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="user" label="发布人" width="120" align="center">
          <template slot-scope="scope">
            <el-tag type="success" size="small">{{ scope.row.user }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="任务状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计划发布时间" width="180" align="center">
          <template slot-scope="scope">
            <div class="time-cell">
              <i class="el-icon-time"></i>
              <span>{{ formatTime(scope.row.scheduledPublishTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="延迟时间" width="120" align="center">
          <template slot-scope="scope">
            <el-tag type="warning" size="small">{{ formatDelayTime(scope.row.delayMilliseconds) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="实际发布时间" width="180" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.publishTime" class="time-cell">
              <i class="el-icon-time"></i>
              <span>{{ formatTime(scope.row.publishTime) }}</span>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-view"
              @click="viewDelayedTaskDetail(scope.row.id)"
              circle
            ></el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleDelayedTaskSizeChange"
          @current-change="handleDelayedTaskCurrentChange"
          :current-page="delayedTaskPageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="delayedTaskPageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="delayedTaskTotal"
        >
        </el-pagination>
      </div>
    </el-card>

    <!-- 对话框部分 -->
    <el-dialog 
      :title="form.id ? '编辑公告' : '新增公告'" 
      :visible.sync="dialogFormVisible" 
      width="50%" 
      :close-on-click-modal="false"
      class="notice-dialog"
      :before-close="handleDialogClose"
    >
      <div class="dialog-header">
        <i class="el-icon-edit-outline"></i>
        <span>{{ form.id ? '编辑公告信息' : '创建新公告' }}</span>
      </div>
      
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="small"
        class="notice-form"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="标题" prop="name">
              <el-input 
                v-model="form.name" 
                placeholder="请输入公告标题"
                prefix-icon="el-icon-edit"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="内容" prop="content">
              <el-input 
                type="textarea" 
                v-model="form.content" 
                placeholder="请输入公告内容"
                :rows="4"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布时间" prop="time">
              <el-date-picker 
                v-model="form.time" 
                type="datetime" 
                value-format="yyyy-MM-dd HH:mm:ss" 
                placeholder="选择发布时间"
                style="width: 100%"
                prefix-icon="el-icon-time"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布人" prop="user">
              <el-input 
                v-model="form.user" 
                placeholder="请输入发布人"
                prefix-icon="el-icon-user"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="封面图片">
              <el-upload 
                :action="uploadUrl" 
                ref="img" 
                :on-success="handleImgUploadSuccess"
                :before-upload="beforeImgUpload"
                :headers="headers"
                :file-list="fileList"
                list-type="picture-card"
                accept=".jpg,.jpeg,.png,.gif"
                :on-error="handleImgUploadError"
                :on-remove="handleImgRemove"
                :before-remove="beforeImgRemove"
                :limit="1"
                class="image-uploader"
              >
                <i class="el-icon-plus"></i>
                <div slot="tip" class="el-upload__tip">
                  <i class="el-icon-info"></i>
                  只能上传jpg/jpeg/png/gif格式的图片，且不超过2MB
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取 消
        </el-button>
        <el-button type="primary" @click="save" :loading="saveLoading" class="confirm-btn">
          <i class="el-icon-check"></i>
          确 定
        </el-button>
      </div>
    </el-dialog>

    <!-- 定时发布对话框 -->
    <el-dialog 
      title="定时发布公告" 
      :visible.sync="scheduleDialogVisible" 
      width="50%" 
      :close-on-click-modal="false"
      class="schedule-dialog"
    >
      <div class="dialog-header">
        <i class="el-icon-alarm-clock"></i>
        <span>设置公告定时发布任务</span>
      </div>
      
      <el-form
        ref="scheduleForm"
        :model="scheduleForm"
        :rules="scheduleRules"
        label-width="120px"
        size="small"
        class="schedule-form"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="公告标题" prop="title">
              <el-input 
                v-model="scheduleForm.title" 
                placeholder="请输入公告标题"
                prefix-icon="el-icon-edit"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="公告内容" prop="content">
              <el-input 
                type="textarea" 
                v-model="scheduleForm.content" 
                placeholder="请输入公告内容"
                :rows="4"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布方式" prop="scheduleType">
              <el-radio-group v-model="scheduleForm.scheduleType">
                <el-radio label="delay">延迟发布</el-radio>
                <el-radio label="datetime">指定时间</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" v-if="scheduleForm.scheduleType === 'delay'">
          <el-col :span="24">
            <el-form-item label="延迟时间" prop="delayMinutes">
              <el-input-number 
                v-model="scheduleForm.delayMinutes" 
                :min="1" 
                :max="10080"
                placeholder="请输入延迟分钟数"
                style="width: 100%"
              ></el-input-number>
              <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                延迟时间范围：1分钟 - 7天（10080分钟）
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" v-if="scheduleForm.scheduleType === 'datetime'">
          <el-col :span="24">
            <el-form-item label="发布时间" prop="scheduledTime">
              <el-date-picker 
                v-model="scheduleForm.scheduledTime" 
                type="datetime" 
                value-format="timestamp" 
                placeholder="选择发布时间"
                style="width: 100%"
                prefix-icon="el-icon-time"
                :picker-options="pickerOptions"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布人" prop="user">
              <el-input 
                v-model="scheduleForm.user" 
                placeholder="请输入发布人"
                prefix-icon="el-icon-user"
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="封面图片">
              <el-upload 
                :action="uploadUrl" 
                ref="scheduleImg" 
                :on-success="handleScheduleImgUploadSuccess"
                :before-upload="beforeImgUpload"
                :headers="headers"
                :file-list="scheduleFileList"
                list-type="picture-card"
                accept=".jpg,.jpeg,.png,.gif"
                :on-error="handleImgUploadError"
                :on-remove="handleScheduleImgRemove"
                :before-remove="beforeImgRemove"
                :limit="1"
                class="image-uploader"
              >
                <i class="el-icon-plus"></i>
                <div slot="tip" class="el-upload__tip">
                  <i class="el-icon-info"></i>
                  只能上传jpg/jpeg/png/gif格式的图片，且不超过2MB
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="scheduleDialogVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取 消
        </el-button>
        <el-button type="success" @click="saveSchedule" :loading="scheduleSaveLoading" class="confirm-btn">
          <i class="el-icon-check"></i>
          确 定
        </el-button>
      </div>
    </el-dialog>

    <!-- 延迟发布详情对话框 -->
    <el-dialog 
      title="延迟发布详情" 
      :visible.sync="delayedDetailVisible" 
      width="60%" 
      :close-on-click-modal="false"
      class="delayed-detail-dialog"
    >
      <div v-if="delayedTaskDetail" class="delayed-detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务ID" :span="1">
            <el-tag type="info">{{ delayedTaskDetail.id }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="公告ID" :span="1">
            <el-tag type="primary">{{ delayedTaskDetail.noticeId || '新建公告' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="公告标题" :span="2">
            <div class="detail-title">{{ delayedTaskDetail.title }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="公告内容" :span="2">
            <div class="detail-content">{{ delayedTaskDetail.content }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="发布人" :span="1">
            <el-tag type="success">{{ delayedTaskDetail.user }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="任务状态" :span="1">
            <el-tag :type="getStatusTagType(delayedTaskDetail.status)">
              {{ getStatusText(delayedTaskDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="1">
            <div class="detail-time">{{ formatTime(delayedTaskDetail.createTime) }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="计划发布时间" :span="1">
            <div class="detail-time">{{ formatTime(delayedTaskDetail.scheduledPublishTime) }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="延迟时间" :span="1">
            <el-tag type="warning">{{ formatDelayTime(delayedTaskDetail.delayMilliseconds) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="实际发布时间" :span="1" v-if="delayedTaskDetail.publishTime">
            <div class="detail-time">{{ formatTime(delayedTaskDetail.publishTime) }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="封面图片" :span="2" v-if="delayedTaskDetail.img">
            <el-image
              style="width: 200px; height: 200px"
              :src="formatImageUrl(delayedTaskDetail.img)"
              :preview-src-list="[formatImageUrl(delayedTaskDetail.img)]"
              fit="cover"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </el-descriptions-item>
          <el-descriptions-item label="任务描述" :span="2" v-if="delayedTaskDetail.description">
            <div class="detail-description">{{ delayedTaskDetail.description }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else class="no-detail">
        <el-empty description="该公告没有关联的延迟发布任务"></el-empty>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="delayedDetailVisible = false" class="close-btn">
          <i class="el-icon-close"></i>
          关 闭
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Notice",
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
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      loading: false,
      saveLoading: false,
      scheduleDialogVisible: false,
      scheduleSaveLoading: false,
      scheduleForm: {
        scheduleType: 'delay',
        delayMinutes: 30,
        title: '',
        content: '',
        user: '',
        img: '',
        scheduledTime: null
      },
      scheduleFileList: [],
      delayedDetailVisible: false,
      delayedTaskDetail: null,
      activeTab: 'notice',
      delayedTaskData: [],
      delayedTaskTotal: 0,
      delayedTaskPageNum: 1,
      delayedTaskPageSize: 10,
      delayedTaskLoading: false,
      delayedTaskSearchTitle: '',
      delayedTaskStatusFilter: null,
      scheduleRules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        delayMinutes: [
          { required: true, message: '请输入延迟时间', trigger: 'blur' },
          { type: 'number', min: 1, max: 10080, message: '延迟时间必须在1-10080分钟之间', trigger: 'blur' }
        ],
        scheduledTime: [
          { required: true, message: '请选择发布时间', trigger: 'change' }
        ],
        user: [
          { required: true, message: '请输入发布人', trigger: 'blur' }
        ]
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7; // 不能选择今天之前的日期
        }
      },
      uploadUrl: '/api/file/upload?folder=notice', // 使用公共文件上传接口
      fileList: [], // 新增文件列表
      headers: {}, // 新增headers对象
      rules: {
        name: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
        ],
        time: [
          { required: true, message: '请选择发布时间', trigger: 'change' }
        ],
        user: [
          { required: true, message: '请输入发布人', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.load()
    // 初始化headers
    this.headers = {
      'token': this.user?.token,
      'Authorization': `Bearer ${this.user?.token}`
      // 移除Content-Type设置，element-ui的upload组件会自动处理
    }
  },
  methods: {
    load() {
      this.loading = true
      this.request.get("/api/notice/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        if (res && res.records !== undefined && res.total !== undefined) {
          this.tableData = res.records || []
          this.total = res.total || 0
          
          // 调试日志：检查图片URL格式
          console.log('=== 公告列表数据调试 ===')
          console.log('完整响应数据:', res)
          console.log('表格数据:', this.tableData)
          
          this.tableData.forEach((item, index) => {
            console.log(`--- 公告${index + 1} 详细信息 ---`)
            console.log('ID:', item.id)
            console.log('标题:', item.name)
            console.log('原始图片URL:', item.img)
            console.log('图片URL类型:', typeof item.img)
            console.log('图片URL长度:', item.img ? item.img.length : 0)
            
            if (item.img) {
              const formattedUrl = this.formatImageUrl(item.img)
              console.log('格式化后URL:', formattedUrl)
              console.log('是否以/api/file/开头:', formattedUrl.startsWith('/api/file/'))
            } else {
              console.log('该公告没有图片')
            }
          })
        } else {
          this.tableData = []
          this.total = 0
          this.$message.warning('获取数据失败，返回数据格式不正确')
        }
      }).catch(err => {
        this.tableData = []
        this.total = 0
        this.$message.error('获取数据失败：' + (err.message || '未知错误'))
      }).finally(() => {
        this.loading = false
      })
    },
    save() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          // 根据是否有id字段决定是新增还是修改
          const requestMethod = this.form.id ? 'put' : 'post';
          this.request[requestMethod]("/api/notice", this.form).then(res => {
            // 改进的成功判断逻辑，适应不同格式的响应
            console.log('保存响应数据:', res);
            if (
              res.code === '200' || 
              res.code === 200 || 
              (res && typeof res === 'object' && Object.keys(res).length > 0) || 
              res === true
            ) {
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.load()
            } else {
              console.warn('保存响应格式不符合预期:', res)
              this.$message.error(res.msg || "保存失败")
            }
          }).catch(err => {
            console.error('保存请求异常:', err)
            this.$message.error("保存请求异常：" + (err.message || '未知错误'))
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
        time: new Date().toISOString().slice(0, 19).replace('T', ' '),
        user: this.user.nickname || ''
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
        if(this.$refs.img) {
          this.$refs.img.clearFiles()
        }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
       this.$nextTick(() => {
         if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
       })
    },
    del(id) {
      this.request.delete("/api/notice/" + id).then(res => {
        // 改进的成功判断逻辑，适应不同格式的响应
        console.log('删除响应数据:', res);
        if (
          res.code === '200' || 
          res.code === 200 || 
          (res && typeof res === 'object' && Object.keys(res).length > 0) || 
          res === true
        ) {
          this.$message.success("删除成功")
          this.load()
        } else {
          console.warn('删除响应格式不符合预期:', res)
          this.$message.error(res.msg || "删除失败")
        }
      }).catch(err => {
        console.error('删除请求异常:', err)
        this.$message.error("删除请求异常：" + (err.message || '未知错误'))
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
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/api/notice/del/batch", ids).then(res => {
        // 改进的成功判断逻辑，适应不同格式的响应
        console.log('批量删除响应数据:', res);
        if (
          res.code === '200' || 
          res.code === 200 || 
          (res && typeof res === 'object' && Object.keys(res).length > 0) || 
          res === true
        ) {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          console.warn('批量删除响应格式不符合预期:', res)
          this.$message.error(res.msg || "批量删除失败")
        }
      }).catch(err => {
        console.error('批量删除请求异常:', err)
        this.$message.error("批量删除请求异常：" + (err.message || '未知错误'))
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
    handleFileUploadSuccess(res) {
      this.form.file = res
    },
    beforeImgUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    handleImgUploadSuccess(response) {
      console.log('图片上传响应:', response);
      
      // 检查响应状态
      if (response.code === '200' || response.code === 200) {
        // 从响应中获取图片URL
        let imgUrl = '';
        if (typeof response.data === 'string') {
          imgUrl = response.data;
        } else if (response.data && response.data.url) {
          imgUrl = response.data.url;
        }
        
        if (imgUrl) {
          this.form.img = imgUrl;
          this.$message.success('图片上传成功');
        } else {
          this.$message.error('图片上传失败：无效的响应格式');
        }
      } else {
        this.$message.error(response.message || '图片上传失败');
      }
    },
    handleTabClick(tab) {
      if (tab.name === 'delayed') {
        // 切换到延迟发布任务标签时，加载任务列表
        this.loadDelayedTasks()
      }
    },
    loadDelayedTasks() {
      this.delayedTaskLoading = true
      const params = {
        pageNum: this.delayedTaskPageNum,
        pageSize: this.delayedTaskPageSize
      }
      if (this.delayedTaskStatusFilter !== null && this.delayedTaskStatusFilter !== undefined) {
        params.status = this.delayedTaskStatusFilter
      }
      if (this.delayedTaskSearchTitle) {
        params.title = this.delayedTaskSearchTitle
      }
      
      this.request.get('/api/notification/delayed/list', { params }).then(res => {
        this.delayedTaskLoading = false
        if (res.success && res.records) {
          this.delayedTaskData = res.records
          this.delayedTaskTotal = res.total || 0
        } else {
          this.delayedTaskData = []
          this.delayedTaskTotal = 0
        }
      }).catch(err => {
        this.delayedTaskLoading = false
        console.error('加载延迟发布任务列表失败:', err)
        this.$message.error('加载延迟发布任务列表失败：' + (err.message || '未知错误'))
        this.delayedTaskData = []
        this.delayedTaskTotal = 0
      })
    },
    handleDelayedTaskSizeChange(val) {
      this.delayedTaskPageSize = val
      this.delayedTaskPageNum = 1
      this.loadDelayedTasks()
    },
    handleDelayedTaskCurrentChange(val) {
      this.delayedTaskPageNum = val
      this.loadDelayedTasks()
    },
    resetDelayedTaskSearch() {
      this.delayedTaskSearchTitle = ''
      this.delayedTaskStatusFilter = null
      this.delayedTaskPageNum = 1
      this.loadDelayedTasks()
    },
    viewDelayedTaskDetail(taskId) {
      this.delayedDetailVisible = true
      this.delayedTaskDetail = null
      
      // 调用后端接口查询延迟发布任务详情（根据任务ID）
      this.request.get(`/api/notification/delayed/detail/task/${taskId}`).then(res => {
        if (res.success && res.data) {
          this.delayedTaskDetail = res.data
        } else {
          this.$message.info(res.message || '任务不存在')
        }
      }).catch(err => {
        console.error('查询延迟发布详情失败:', err)
        this.$message.error('查询延迟发布详情失败：' + (err.message || '未知错误'))
      })
    },
    viewDelayedDetail(noticeId) {
      this.delayedDetailVisible = true
      this.delayedTaskDetail = null
      
      // 调用后端接口查询延迟发布任务详情
      this.request.get(`/api/notification/delayed/detail/notice/${noticeId}`).then(res => {
        if (res.success && res.data) {
          this.delayedTaskDetail = res.data
        } else {
          this.$message.info(res.message || '该公告没有关联的延迟发布任务')
        }
      }).catch(err => {
        console.error('查询延迟发布详情失败:', err)
        this.$message.error('查询延迟发布详情失败：' + (err.message || '未知错误'))
      })
    },
    formatTime(timestamp) {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    formatDelayTime(milliseconds) {
      if (!milliseconds) return '-'
      const seconds = Math.floor(milliseconds / 1000)
      const minutes = Math.floor(seconds / 60)
      const hours = Math.floor(minutes / 60)
      const days = Math.floor(hours / 24)
      
      if (days > 0) {
        return `${days}天 ${hours % 24}小时 ${minutes % 60}分钟`
      } else if (hours > 0) {
        return `${hours}小时 ${minutes % 60}分钟`
      } else if (minutes > 0) {
        return `${minutes}分钟`
      } else {
        return `${seconds}秒`
      }
    },
    getStatusText(status) {
      const statusMap = {
        0: '待发布',
        1: '已发布',
        2: '已取消',
        3: '发布失败'
      }
      return statusMap[status] || '未知状态'
    },
    getStatusTagType(status) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'info',
        3: 'danger'
      }
      return typeMap[status] || 'info'
    },
    formatImageUrl(imgUrl) {
      if (!imgUrl) return ''
      
      // 调试日志
      console.log('formatImageUrl input:', imgUrl)
      
      // 如果是MinIO的URL格式（/api/file/xxx），直接返回（通过网关访问）
      if (imgUrl.startsWith('/api/file/')) {
        console.log('formatImageUrl output (direct return):', imgUrl)
        return imgUrl
      }
      
      // 如果包含localhost，提取文件名并转换为API路径
      if (imgUrl.startsWith('http') && imgUrl.includes('localhost')) {
        const fileName = imgUrl.split('/').pop()
        // 检查文件名是否包含扩展名，如果没有则添加默认扩展名
        if (!fileName.includes('.')) {
          return `/api/file/${fileName}.jpg`
        } else {
          return `/api/file/${fileName}`
        }
      }
      
      // 如果是其他HTTP URL，直接返回
      if (imgUrl.startsWith('http')) {
        return imgUrl
      }
      
      // 如果是文件名（如 notice_xxx.jpg），添加API前缀
      if (imgUrl.includes('notice_') || imgUrl.includes('avatar_') || imgUrl.includes('product_')) {
        return `/api/file/${imgUrl}`
      }
      
      // 处理MinIO存储的文件路径格式：folder/filename
      if (imgUrl.includes('/') && !imgUrl.startsWith('http') && !imgUrl.startsWith('/api/')) {
        // 如果包含文件夹路径，直接使用
        const result = `/api/file/${imgUrl}`
        console.log('formatImageUrl output (folder/filename):', result)
        return result
      }
      
      // 其他情况，添加API前缀
      const result = `/api/file/${imgUrl}`
      console.log('formatImageUrl output (default):', result)
      return result
    },
    download(url) {
      window.open(url)
    },
    handleImageError(originalUrl, event) {
      console.error('图片加载失败:', {
        originalUrl: originalUrl,
        formattedUrl: this.formatImageUrl(originalUrl),
        event: event
      })
    },
    handleImageLoad(originalUrl, event) {
      console.log('图片加载成功:', {
        originalUrl: originalUrl,
        formattedUrl: this.formatImageUrl(originalUrl),
        event: event
      })
    },
    exp() {
      window.open("http://localhost:8080/api/notice/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    },
    handleDialogClose() {
      this.$refs.form && this.$refs.form.resetFields()
      this.form = {}
    },
    handleImgUploadError(err, file, fileList) {
      console.error('图片上传错误:', err, file, fileList)
      // 提供更明确的错误信息
      if (err && err.message) {
        this.$message.error('图片上传失败: ' + err.message)
      } else if (err && err.status) {
        this.$message.error('图片上传失败: 服务器错误 ' + err.status)
      } else {
        this.$message.error('图片上传失败，请检查服务器连接或稍后重试')
      }
    },
    handleImgRemove(file, fileList) {
      console.log('移除图片:', file, fileList)
      this.form.img = ''
      this.fileList = fileList
    },
    beforeImgRemove(file, fileList) {
      return this.$confirm(`确定移除 ${ file.name }？`)
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 2 === 0) {
        return 'even-row';
      } else {
        return 'odd-row';
      }
    },
    handleScheduleAdd() {
      this.scheduleDialogVisible = true
      this.scheduleForm = {
        scheduleType: 'delay',
        delayMinutes: 30,
        user: this.user.nickname || ''
      }
      this.$nextTick(() => {
        this.$refs.scheduleForm && this.$refs.scheduleForm.clearValidate()
        if(this.$refs.scheduleImg) {
          this.$refs.scheduleImg.clearFiles()
        }
      })
    },
    saveSchedule() {
      this.$refs.scheduleForm.validate((valid) => {
        if (valid) {
          this.scheduleSaveLoading = true
          
          const requestData = {
            title: this.scheduleForm.title,
            content: this.scheduleForm.content || '',
            user: this.scheduleForm.user,
            img: this.scheduleForm.img
          }
          
          // 成功保存后，如果当前在延迟任务标签页，刷新列表
          const shouldRefreshDelayedList = this.activeTab === 'delayed'
          
          if (this.scheduleForm.scheduleType === 'delay') {
            // 延迟发布
            requestData.delayMinutes = this.scheduleForm.delayMinutes
            this.request.post("/api/notification/delayed/create", requestData).then(res => {
              if (res.success) {
                this.$message.success("定时发布任务创建成功")
                this.scheduleDialogVisible = false
                // 如果在延迟任务标签页，刷新任务列表
                if (shouldRefreshDelayedList) {
                  this.loadDelayedTasks()
                }
                // 如果在公告列表标签页，刷新公告列表
                if (this.activeTab === 'notice') {
                  this.load()
                }
              } else {
                this.$message.error(res.message || "创建定时发布任务失败")
              }
            }).catch(err => {
              this.$message.error("创建定时发布任务失败：" + (err.message || '未知错误'))
            }).finally(() => {
              this.scheduleSaveLoading = false
            })
          } else {
            // 指定时间发布
            requestData.scheduledPublishTime = this.scheduleForm.scheduledTime
            this.request.post("/api/notification/delayed/create-at-time", requestData).then(res => {
              if (res.success) {
                this.$message.success("定时发布任务创建成功")
                this.scheduleDialogVisible = false
                this.load()
              } else {
                this.$message.error(res.message || "创建定时发布任务失败")
              }
            }).catch(err => {
              this.$message.error("创建定时发布任务失败：" + (err.message || '未知错误'))
            }).finally(() => {
              this.scheduleSaveLoading = false
            })
          }
        } else {
          this.$message.warning('请完善表单信息')
          return false
        }
      })
    },
    handleScheduleImgUploadSuccess(response) {
      console.log('定时发布图片上传响应:', response);
      if (response.code === '200' || response.code === 200) {
        let imgUrl = '';
        if (typeof response.data === 'string') {
          imgUrl = response.data;
        } else if (response.data && response.data.url) {
          imgUrl = response.data.url;
        }
        if (imgUrl) {
          this.scheduleForm.img = imgUrl;
          console.log('定时发布图片URL设置:', imgUrl);
          this.$message.success('图片上传成功');
        } else {
          this.$message.error('图片上传失败：无效的响应格式');
        }
      } else {
        this.$message.error(response.message || '图片上传失败');
      }
    },
    handleScheduleImgRemove(file, fileList) {
      console.log('移除定时发布图片:', file, fileList)
      this.scheduleForm.img = ''
      this.scheduleFileList = fileList
    }
  }
}
</script>

<style lang="scss" scoped>
.notice-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);

  .page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 24px;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
    
    .header-content {
      .title-section {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        
        .title-icon {
          font-size: 28px;
          margin-right: 12px;
          color: #ffd700;
        }
        
        h2 {
          font-size: 28px;
          font-weight: 600;
          margin: 0;
          text-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
      }
      
      .subtitle {
        color: rgba(255, 255, 255, 0.9);
        font-size: 16px;
        margin: 0;
        font-weight: 300;
      }
    }
    
    .header-stats {
      display: flex;
      gap: 24px;
      
      .stat-item {
        text-align: center;
        
        .stat-number {
          font-size: 32px;
          font-weight: bold;
          color: #ffd700;
          text-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        
        .stat-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
          margin-top: 4px;
        }
      }
    }
  }

  .search-card {
    margin-bottom: 24px;
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 20px rgba(0,0,0,0.08);
    
    .search-wrapper {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;

      .search-section {
        .search-input {
          width: 350px;
          
          ::v-deep .el-input__inner {
            border-radius: 8px;
            border: 2px solid #e4e7ed;
            transition: all 0.3s;
            
            &:focus {
              border-color: #667eea;
              box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
            }
          }
        }
      }

      .button-group {
        display: flex;
        gap: 12px;
        
        .action-btn {
          border-radius: 8px;
          padding: 10px 20px;
          font-weight: 500;
          transition: all 0.3s;
          display: flex;
          align-items: center;
          gap: 6px;
          
          i {
            font-size: 16px;
          }
          
          &.primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
            }
          }
          
          &.warning {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border: none;
            color: white;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(245, 87, 108, 0.4);
            }
          }
          
          &.danger {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            border: none;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
            }
          }
          
          &.success {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            border: none;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(56, 239, 125, 0.4);
            }
          }
        }
      }
    }
  }

  .table-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 20px rgba(0,0,0,0.08);
    
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 0;
      border-bottom: 2px solid #f0f2f5;
      margin-bottom: 16px;
      
      .table-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin: 0;
        
        i {
          color: #667eea;
          font-size: 20px;
        }
      }
      
      .table-actions {
        .el-button {
          border-radius: 8px;
          transition: all 0.3s;
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
          }
        }
      }
    }
    
    .notice-table {
      margin: 0;
      border-radius: 8px;
      overflow: hidden;
      
      // 表格单元格样式
      .title-cell {
        display: flex;
        align-items: center;
        gap: 8px;
        
        i {
          color: #667eea;
          font-size: 16px;
        }
        
        .title-text {
          font-weight: 500;
          color: #303133;
        }
      }
      
      .content-cell {
        .content-text {
          color: #606266;
          line-height: 1.5;
        }
      }
      
      .time-cell, .user-cell {
        display: flex;
        align-items: center;
        gap: 6px;
        
        i {
          color: #909399;
          font-size: 14px;
        }
      }
      
      .image-cell {
        .notice-image {
          border-radius: 8px;
          transition: all 0.3s;
          box-shadow: 0 2px 8px rgba(0,0,0,0.1);
          
          &:hover {
            transform: scale(1.1);
            box-shadow: 0 4px 16px rgba(0,0,0,0.2);
          }
        }

        .no-image {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
          color: #c0c4cc;
          font-size: 12px;
          
          i {
            font-size: 24px;
          }
        }

        .image-slot {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
          color: #c0c4cc;
          font-size: 20px;
          border-radius: 8px;
        }
      }
      
      .action-buttons {
        display: flex;
        gap: 8px;
        justify-content: center;
        
        .action-btn {
          border-radius: 50%;
          transition: all 0.3s;
          
          &.edit-btn:hover {
            transform: translateY(-2px) scale(1.1);
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
          }
          
          &.delete-btn:hover {
            transform: translateY(-2px) scale(1.1);
            box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
          }
        }
      }
    }
  }

  .pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
    
    ::v-deep .el-pagination {
      .el-pager li {
        border-radius: 6px;
        margin: 0 2px;
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
        }
      }
      
      .btn-prev, .btn-next {
        border-radius: 6px;
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
        }
      }
    }
  }
}

// 对话框样式
.notice-dialog {
  ::v-deep .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.2);
    
    .el-dialog__header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 16px 16px 0 0;
      padding: 20px 24px;
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
      }
      
      .el-dialog__close {
        color: white;
        font-size: 20px;
        
        &:hover {
          color: #ffd700;
        }
      }
    }
    
    .el-dialog__body {
      padding: 24px;
    }
    
    .el-dialog__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #f0f2f5;
    }
  }
  
  .dialog-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 20px;
    
    i {
      color: #667eea;
      font-size: 18px;
    }
  }
  
  .notice-form {
    .el-form-item {
      margin-bottom: 20px;
      
      .el-form-item__label {
        font-weight: 500;
        color: #606266;
      }
      
      .el-input__inner, .el-textarea__inner {
        border-radius: 8px;
        border: 2px solid #e4e7ed;
        transition: all 0.3s;
        
        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
        }
      }
    }
  }
  
  .image-uploader {
    ::v-deep .el-upload {
      border: 2px dashed #d9d9d9;
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;
      
      &:hover {
        border-color: #667eea;
        background: rgba(102, 126, 234, 0.05);
      }
    }
    
    ::v-deep .el-upload__tip {
      color: #909399;
      font-size: 12px;
      margin-top: 8px;
      
      i {
        margin-right: 4px;
        color: #667eea;
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .cancel-btn, .confirm-btn {
      border-radius: 8px;
      padding: 10px 24px;
      font-weight: 500;
      transition: all 0.3s;
      display: flex;
      align-items: center;
      gap: 6px;
      
      i {
        font-size: 16px;
      }
    }
    
    .cancel-btn {
      background: #f5f7fa;
      border: 2px solid #e4e7ed;
      color: #606266;
      
      &:hover {
        background: #e4e7ed;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      }
    }
    
    .confirm-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

// 响应式设计
@media screen and (max-width: 768px) {
  .notice-container {
    padding: 16px;

    .page-header {
      flex-direction: column;
      text-align: center;
      gap: 16px;
      
      .header-stats {
        justify-content: center;
      }
    }

    .search-wrapper {
      flex-direction: column;
      align-items: stretch !important;

      .search-section .search-input {
        width: 100% !important;
      }

      .button-group {
        justify-content: space-between;
        flex-wrap: wrap;
        
        .action-btn {
          flex: 1;
          min-width: 120px;
        }
      }
    }
    
    .table-card {
      .table-header {
        flex-direction: column;
        gap: 12px;
        text-align: center;
      }
    }
  }
  
  .notice-dialog {
    ::v-deep .el-dialog {
      width: 95% !important;
      margin: 5vh auto !important;
    }
  }
}

// 表格行样式
::v-deep .el-table {
  .even-row {
    background-color: #fafbfc;
  }
  
  .odd-row {
    background-color: #ffffff;
  }
  
  .headerBg {
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%) !important;
    color: #495057;
    font-weight: 600;
    font-size: 14px;
  }
  
  th {
    background-color: #f8f9fa !important;
    border-bottom: 2px solid #dee2e6 !important;
  }
  
  td {
    padding: 16px 0;
    border-bottom: 1px solid #f0f2f5 !important;
  }
  
  tr:hover > td {
    background-color: rgba(102, 126, 234, 0.05) !important;
  }
}

// 标签样式优化
::v-deep .el-tag {
  border-radius: 16px;
  padding: 0 12px;
  height: 28px;
  line-height: 26px;
  font-weight: 500;
  border: none;
  
  &.el-tag--info {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    color: #1976d2;
  }
  
  &.el-tag--success {
    background: linear-gradient(135deg, #e8f5e8 0%, #c8e6c9 100%);
    color: #388e3c;
  }
  
  &.el-tag--warning {
    background: linear-gradient(135deg, #fff3e0 0%, #ffcc02 100%);
    color: #f57c00;
  }
  
  &.el-tag--danger {
    background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
    color: #d32f2f;
  }
}

// 标签页样式
.notice-tabs {
  margin-bottom: 20px;
  
  ::v-deep .el-tabs__header {
    margin-bottom: 0;
  }
  
  ::v-deep .el-tabs__item {
    font-size: 16px;
    font-weight: 500;
    padding: 0 30px;
    
    i {
      margin-right: 8px;
    }
  }
}

// 全局动画效果
.el-button, .el-card, .el-table, .el-pagination {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
}

// 延迟发布详情对话框样式
.delayed-detail-dialog {
  .delayed-detail-content {
    .detail-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      padding: 8px 0;
    }
    
    .detail-content {
      font-size: 14px;
      color: #606266;
      line-height: 1.8;
      padding: 8px 0;
      white-space: pre-wrap;
      word-break: break-word;
    }
    
    .detail-time {
      font-size: 14px;
      color: #606266;
      padding: 4px 0;
    }
    
    .detail-description {
      font-size: 14px;
      color: #909399;
      font-style: italic;
      padding: 8px 0;
    }
  }
  
  .no-detail {
    padding: 40px 0;
    text-align: center;
  }
  
  .close-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
  }
}

// 操作按钮中的详情按钮样式
.action-btn.detail-btn {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  border: none;
  
  &:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(116, 185, 255, 0.6);
  }
}
</style>

