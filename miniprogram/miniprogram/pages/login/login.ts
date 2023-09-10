// pages/login/login.ts
Page({

  /**
   * 页面的初始数据
   */
  data: {
    uuid: ''

  },
  login() {
    let that = this
    wx.login({
      success (res) {
        if (res.code) {
          //发起网络请求
          wx.request({
            url: 'https://mubangou.com/api/login/loginByCode.php',
            //url: 'http://localhost:998/api/login/loginByCode.php',
            method: "GET",
            data: {
              code: res.code,
              uuid: that.data.uuid
            },
            header: {
              'content-type': 'application/json'
            },
            success (login) {
              console.log(login)
              if (login.data.login && login.data.login > 0) {
                wx.showToast({
                  title: '登录成功',
                  icon: 'success',
                  duration: 2000
                })
                wx.redirectTo({
                  url: "/pages/success/success"
                })
              } else {
                wx.showToast({
                  title: '登录失败',
                  icon: 'error',
                  duration: 2000
                })
              }
            }
          })
        } else {
          wx.showToast({
            title: '未知原因失败',
            icon: 'error',
            duration: 2000
          })
        }
      }
    })
  },
  notLogin() {
    wx.showToast({
      title: '已取消登录',
      icon: 'error',
      duration: 2000
    })
    wx.redirectTo({
      url: "/pages/index/index"
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad (query) {
    const scene = decodeURIComponent(query.scene + '')
    this.data.uuid = scene
    if (!query.scene) {
      wx.redirectTo({
        url: "/pages/index/index"
      })
    }
    wx.hideShareMenu({
      menus: ['shareAppMessage', 'shareTimeline']
    })
    // 绑定分享参数
    wx.onCopyUrl(() => {
      return { query: 'a=1&b=2' }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})