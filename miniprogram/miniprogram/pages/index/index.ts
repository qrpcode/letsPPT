// index.ts
// 获取应用实例
const app = getApp<IAppOption>()

Page({
  data: {
  },
  // 事件处理函数
  bindViewTap() {
  },
  onLoad() {

  },
  getUserProfile() {

  },
  getUserInfo() {

  },
  copyLink() {
    wx.setClipboardData({
      data: "https://mubangou.com",
      success: function () {
        wx.showToast({
          title: '复制成功',
          icon: "none"
        })
      }
    })
  },
  scanCode() {
    wx.getSystemInfo({
      success: (res) => {
        if (res.platform && (res.platform == 'windows' || res.platform == 'mac')) {
          wx.showToast({
            title: '电脑暂不支持，请使用手机扫码哦~',
            icon: "none"
          })
        } else {

          wx.scanCode({
            onlyFromCamera: true,
            success(res) {
              if (res.path && res.path.indexOf("pages/login/login?scene=") == 0) {
                console.log(res)
                wx.redirectTo({
                  url: "/" + res.path,
                })
              } else {
                wx.showToast({
                  title: '亲，要扫描模板狗网站上的小程序码呀~',
                  icon: "none"
                })
              }
            }
          })
        }
      }
    });
  },
})
