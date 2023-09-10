// pages/pay/pay.ts
Page({

  /**
   * 页面的初始数据
   */
  data: {
    payData: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(query) {
    let that = this;
    const scene = decodeURIComponent(query.scene + '')
    console.log("scene:" + scene)
    wx.login({
      success(res) {
        console.log("code:" + res.code)
        if (res.code) {
          //发起网络请求
          wx.request({
            url: 'https://mubangou.com/api/pay.php?' + scene,
            //url: 'https://mubangou.com/api/pay/pay.php',
            method: "GET",
            data: {
              code: res.code,
            },
            header: {
              'content-type': 'application/json'
            },
            success(login) {
              console.log(login)
              that.data.payData = login.data;
              wx.requestPayment(
                {
                  "appid": "wx69662b82e934386e",
                  "timeStamp": login.data.timeStamp + '',
                  "nonceStr": login.data.nonceStr,
                  "package": login.data.package,
                  "signType": "MD5",
                  "paySign": login.data.paySign,
                  "success": function () {
                    wx.showModal({
                      showCancel: false,
                      title: '支付成功',
                      content: '您已经支付成功，回到网页下载吧~'
                    })
                  },
                  "fail": function (res) {
                    console.log(res)
                    wx.showModal({
                      showCancel: false,
                      title: '支付失败',
                      content: '关闭小程序重新扫码可重新发起支付'
                    })
                  }
                })
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

  },

  pay() {

  }
})