<template>
  <el-card
      class="simple-weather-card"
      shadow="hover"
      :style="{background: 'linear-gradient(145deg, #f8f9fa, #e9ecef)'}"
  >
    <template #header>
      <div class="weather-header">
        <i class="el-icon-sunny" style="color: #FFC107;"></i>
        <span style="color: #6B8DD6;">实时天气</span>
        <el-tag type="success" effect="dark" size="small">Live</el-tag>
      </div>
    </template>

    <!-- 加载状态 -->
    <div v-if="loading" class="weather-loading animate__animated animate__fadeIn">
      <i class="el-icon-loading" style="font-size: 24px;"></i>
      <div class="loading-text">正在获取天气信息...</div>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="weather-error animate__animated animate__shakeX">
      <i class="el-icon-warning" style="color: #F56C6C;"></i>
      <div class="error-message">{{ error }}</div>
      <el-button
          type="primary"
          size="mini"
          @click="getWeather"
          class="retry-btn"
      >
        重试
      </el-button>
    </div>

    <!-- 未选择农田状态 -->
    <div v-else-if="!farmId" class="weather-empty animate__animated animate__fadeIn">
      <i class="el-icon-map-location" style="font-size: 48px; color: #C0C4CC;"></i>
      <div class="empty-text">请先选择农田</div>
    </div>

    <!-- 正常天气显示 -->
    <div v-else class="weather-content animate__animated animate__fadeIn">
      <div class="main-info">
        <div class="temperature-wrapper">
          <div class="temperature">
            {{ weatherData.temperature }}℃
          </div>
          <div class="feels-like">体感 {{ weatherData.feelsLike }}℃</div>
        </div>
        <div class="weather-status">
          <i :class="weatherIcon" style="font-size: 48px;"></i>
          <div class="weather-description">{{ weatherData.description }}</div>
        </div>
      </div>

      <el-divider class="custom-divider"><i class="el-icon-data-line"></i></el-divider>

      <div class="extra-info">
        <div class="info-item">
          <i class="el-icon-watermelon" style="color: #67C23A;"></i>
          <div class="info-text">
            <div class="info-label">湿度</div>
            <div class="info-value">{{ weatherData.humidity }}%</div>
          </div>
        </div>
        <div class="info-item">
          <i class="el-icon-wind-power" style="color: #409EFF;"></i>
          <div class="info-text">
            <div class="info-label">风速</div>
            <div class="info-value">{{ weatherData.windSpeed }} m/s</div>
          </div>
        </div>
        <div class="info-item">
          <i class="el-icon-timer" style="color: #E6A23C;"></i>
          <div class="info-text">
            <div class="info-label">气压</div>
            <div class="info-value">{{ weatherData.pressure }} hPa</div>
          </div>
        </div>
        <div class="info-item">
          <i class="el-icon-view" style="color: #909399;"></i>
          <div class="info-text">
            <div class="info-label">光照</div>
            <div class="info-value">{{ weatherData.light }} lux</div>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { request } from "@/utils/request";

export default {
  name: 'SimpleWeather',
  props: {
    farmId: {
      type: Number,
      required: false,
      default: null
    }
  },
  data() {
    return {
      loading: false,
      error: null,
      weatherData: {
        temperature: 0,
        feelsLike: 0,
        humidity: 0,
        windSpeed: 0,
        pressure: 0,
        light: 0,
        description: '晴朗'
      }
    }
  },
  computed: {
    weatherIcon() {
      // 根据天气状况返回不同Element UI图标
      if (this.weatherData.temperature > 28) {
        return 'el-icon-sunny';
      } else if (this.weatherData.humidity > 70) {
        return 'el-icon-rainy';
      } else {
        return 'el-icon-cloudy';
      }
    }
  },
  watch: {
    farmId: {
      handler(newVal) {
        if (newVal) {
          this.getWeather();
        }
      },
      immediate: true
    }
  },
  methods: {
    async getWeather() {
      if (!this.farmId) return;

      this.loading = true;
      this.error = null;

      try {
        const response = await request.get('/weather/recent', {
          params: {
            farmId: this.farmId,
            days: 1
          }
        });

        if (response.code === '200' && response.data.length > 0) {
          const latestData = response.data[0];
          // 映射后端数据到前端模型
          this.weatherData = {
            temperature: latestData.temperature || 0,
            feelsLike: latestData.feelsLike || 0,
            humidity: latestData.humidity || 0,
            windSpeed: latestData.windSpeed || 0,
            pressure: latestData.pressure || 0,
            light: latestData.light || 0,
            description: this.getWeatherDescription(latestData.temperature, latestData.humidity)
          };
        } else {
          this.error = '未找到该农田的天气数据';
        }
      } catch (error) {
        console.error('天气数据获取失败:', error);
        this.error = '获取天气数据失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },

    getWeatherDescription(temp, humidity) {
      if (temp > 30) return '炎热';
      if (temp > 20) return '温暖';
      if (temp > 10) return '凉爽';
      if (temp > 0) return '寒冷';
      return '严寒';
    }
  }
}
</script>

<style scoped>
.simple-weather-card {
  margin: 10px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0,0,0,0.05);
}

.simple-weather-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.08);
}

.weather-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: rgba(107, 141, 214, 0.05);
  border-radius: 12px 12px 0 0;
}

.weather-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 320px;
  gap: 16px;
}

.loading-text {
  color: #6B8DD6;
  font-size: 14px;
  animation: pulse 1.5s infinite;
}

.weather-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 320px;
  gap: 16px;
  text-align: center;
}

.error-message {
  color: #F56C6C;
  font-size: 14px;
  max-width: 80%;
}

.retry-btn {
  margin-top: 12px;
  padding: 8px 24px;
}

.weather-content {
  padding: 16px 24px;
}

.main-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0;
}

.temperature-wrapper {
  text-align: center;
}

.temperature {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}

.feels-like {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.weather-status {
  text-align: center;
}

.weather-icon {
  width: 80px;
  height: 80px;
}

.weather-description {
  font-size: 14px;
  color: #606266;
  margin-top: 8px;
}

.custom-divider {
  margin: 16px 0;
}

.extra-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: rgba(255,255,255,0.8);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.info-text {
  margin-left: 12px;
}

.info-label {
  font-size: 12px;
  color: #909399;
}

.info-value {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}
</style>