// vue.config.js
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
    devServer: {
        https: false,
        port: 8083, // 前端服务端口（保持不变）
        proxy: {
            '/api': {
                // 修改：将 target 改为网关端口 8080
                target: 'http://localhost:8080',
                changeOrigin: true, // 允许跨域（必要）
                secure: false, // 非 HTTPS 环境下设为 false
                ws: true, // 支持 WebSocket（如果不需要可删除）
                logLevel: 'debug', // 打印代理日志（方便调试）
                onProxyReq: function(proxyReq, req, res) {
                    console.log('API代理请求详情:', {
                        method: req.method,
                        originalUrl: req.originalUrl,
                        target: 'http://localhost:8080/api' // 同步更新日志中的目标地址
                    });
                    // 注意：以下两行设置可能多余，甚至导致问题，建议删除
                    // proxyReq.setHeader('Host', 'localhost:8080');
                    // proxyReq.setHeader('Origin', 'http://localhost:8080');
                },
                onProxyRes: function(proxyRes, req, res) {
                    console.log('API代理响应详情:', {
                        statusCode: proxyRes.statusCode,
                        url: req.url,
                        target: 'http://localhost:8080/api' // 同步更新日志中的目标地址
                    });
                },
                onError: function(err, req, res) {
                    console.error('API代理错误详情:', {
                        error: err.message,
                        url: req.url,
                        target: 'http://localhost:8081/api' // 同步更新日志中的目标地址
                    });
                    res.writeHead(500, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({
                        error: 'API代理错误',
                        message: `无法连接到网关，请检查 http://localhost:8081 是否启动`
                    }));
                }
            },
            '/predict': {
                // 此代理指向 8000 端口，若该服务正常则无需修改
                target: 'http://localhost:8000',
                changeOrigin: true,
                secure: false,
                ws: true,
                logLevel: 'debug',
                onProxyReq: function(proxyReq, req, res) {
                    console.log('预测代理请求详情:', {
                        method: req.method,
                        originalUrl: req.originalUrl,
                        url: req.url,
                        headers: proxyReq.getHeaders(),
                        target: 'http://localhost:8000/predict'
                    });
                    
                    proxyReq.setHeader('Host', 'localhost:8000');
                    proxyReq.setHeader('Origin', 'http://localhost:8000');
                },
                onProxyRes: function(proxyRes, req, res) {
                    console.log('预测代理响应详情:', {
                        statusCode: proxyRes.statusCode,
                        statusMessage: proxyRes.statusMessage,
                        headers: proxyRes.headers,
                        url: req.url,
                        target: 'http://localhost:8000/predict'
                    });
                },
                onError: function(err, req, res) {
                    console.error('预测代理错误详情:', {
                        error: err,
                        message: err.message,
                        stack: err.stack,
                        url: req.url,
                        method: req.method,
                        target: 'http://localhost:8000/predict'
                    });
                    
                    res.writeHead(500, {
                        'Content-Type': 'application/json'
                    });
                    res.end(JSON.stringify({
                        error: '预测代理错误',
                        message: err.message
                    }));
                }
            }
        }
    }
})