$(document).ready(function(){
    $(window).scroll(function(){
        let top = $(document).scrollTop();
        if (top > 300) {
            $("#search-head").css({"display": "block"});
        } else {
            $("#search-head").css({"display": "none"});
        }
    })
    
    let box0 = $(".box-0");
    let box1 = $(".box-1");
    let box2 = $(".box-2");
    let box3 = $(".box-3");
    let homeBox0 = $("#home-box-0");
    let homeBox1 = $("#home-box-1");
    let homeBox2 = $("#home-box-2");
    let homeBox3 = $("#home-box-3");
    
    box0.css("display", "block");
    box1.css("display", "none");
    box2.css("display", "none");
    box3.css("display", "none");
    homeBox0.attr("class", "list-tab-hot")
    homeBox1.attr("class", "list-tab")
    homeBox2.attr("class", "list-tab")
    homeBox3.attr("class", "list-tab")

    homeBox0.hover(function() {
        box0.css("display", "block");
        box1.css("display", "none");
        box2.css("display", "none");
        box3.css("display", "none");
        homeBox0.attr("class", "list-tab-hot")
        homeBox1.attr("class", "list-tab")
        homeBox2.attr("class", "list-tab")
        homeBox3.attr("class", "list-tab")
    })
    
    homeBox1.hover(function() {
        box0.css("display", "none");
        box1.css("display", "block");
        box2.css("display", "none");
        box3.css("display", "none");
        homeBox0.attr("class",  "list-tab")
        homeBox1.attr("class", "list-tab-hot")
        homeBox2.attr("class", "list-tab")
        homeBox3.attr("class", "list-tab")
    })
    
    homeBox2.hover(function() {
        box0.css("display", "none");
        box1.css("display", "none");
        box2.css("display", "block");
        box3.css("display", "none");
        homeBox0.attr("class",  "list-tab")
        homeBox1.attr("class", "list-tab")
        homeBox2.attr("class", "list-tab-hot")
        homeBox3.attr("class", "list-tab")
    })
    
    homeBox3.hover(function() {
        box0.css("display", "none");
        box1.css("display", "none");
        box2.css("display", "none");
        box3.css("display", "block");
        homeBox0.attr("class", "list-tab")
        homeBox1.attr("class", "list-tab")
        homeBox2.attr("class", "list-tab")
        homeBox3.attr("class", "list-tab-hot")
    })

});

