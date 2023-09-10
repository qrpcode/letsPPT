let pptFilePath;
let ppt = null;
let index = 0;
let groupIds = [];
let nowChooseId = null;
let thisPageDelete = []
let groups = [];
let groupElements = [];
let page = {};

//启动获取
let styles = [];
let background = [];
let pages = [];

let pagesHtml = ''

let TYPE_IMG = "IMG"
let TYPE_SHAPE = "SHAPE"
let TYPE_TEXT = "TEXT"
let TYPE_LINE = "LINE"
let TYPE_GROUP = "GROUP"

let cmPx = 37.79

let LIST_GROUP = "list"

let filePath;

let TEXT_TYPE_NUMBER = "number"
let TEXT_TYPE_YEAR = "year"
let TEXT_TYPE_LIPSUM = "lipsum"
let TEXT_TYPE_SIGNATURE = "signature"
let TEXT_TYPE_BIG_TITLE = "bigTitle"

let NONE = "none"

$(document).ready(function () {

    getNextFile()
    flushPageElementList()

    $(".ppt-main").click(function (e) {
        let id = $(e.target).attr('id');
        let idName = "#" + id;
        if (groupIds.indexOf(id) === -1) {
            $(idName).css("background", $(idName).css("border-color"))
            $(idName).css("z-index", "0")
            groupIds.push(id)
        } else {
            $(idName).css("background", "none")
            $(idName).css("z-index", "1")
            groupIds.splice(groupIds.indexOf(id), 1)
        }
        showElementBtn(id);
        pptSaveTabShow()
    });

});

/**
 * 展示PPT的保存tab
 */
function pptSaveTabShow() {
    let element = buildGroupSize(groupIds, false);
    let saveTab = $(".save-tab");
    saveTab.css("left", cmPx * element.right - 200)
    saveTab.css("top", cmPx * element.bottom + 65)
    saveTab.css("display", "block")

    //异步查询可能成为的元素
    setElementWithNearlyElement()

    $(".type-choose").html('<div class="group-label-box"> <label> <span>页面</span> ' +
    '<select id = "saveTabPage" onclick="saveTabPageChoose()">' +
    pagesHtml +
    '</select>' +
    '</label> </div><div class="group-label-box"> <label> <span>元素</span> ' +
    '<select id="saveTabElementList">' +
    '<option value=""></option>' +
    '</select>' +
    '</label> </div> ' +
        '<div class="save-tab-ok" onclick="saveGroupWithFind()">OK</div>')
    $("#saveTabPage").val($("#page-page-page").val())
    saveTabPageChoose()
}

function setElementWithNearlyElement() {
    let thisGroupElements = [];
    for (let i = 0; i < groupIds.length; i++) {
        thisGroupElements.push(getElementByGroupId(groupIds, i));
    }
    $.ajax({
        type: "POST",
        url: "http://127.0.0.1:12301/template/build/helper",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "elements": groups,
            "groups": groupElements,
            "page": page,
            "thisGroupElements": thisGroupElements
        }),
        success: function (data) {
            console.log(data)
            let html = ''
            for (let i = 0; i < data.length; i++) {
                html += '<div onclick="saveGroupWithType(\'' + data[i].purpose + '\', \''
                    + data[i].element + '\')">' + data[i].purpose + '|' + data[i].label + '</div>'
            }
            $(".type-infer").html(html)
        }
    });
}

function getPageName() {
    let html = ''
    for (let i = 0; i < pages.length; i++) {
        if (pages[i].pageTypeNameEn === $("#saveTabPage").val()) {
            for (let j = 0; j < pages[i].models.length; j++) {
                html += '<option value="' + pages[i].models[j].id + '">'
                    + pages[i].models[j].pptPageModelName + '</option>'
            }
        }
    }
    return html;
}

function saveTabPageChoose() {
    let html = getPageName();
    $("#saveTabElementList").html(html + '<option value="page">整页（选此不填其他）</option>')
}

function flushPageElementList() {
    $.get(
        "http://localhost:12301/enum/style", {}, function (data) {
            styles = data;
        }
    );

    $.get(
        "http://localhost:12301/enum/background", {}, function (data) {
            background = data;
        }
    );

    $.get(
        "http://localhost:12301/enum/pages", {}, function (data) {
            pages = data;
            let html = '<option value=""></option>'
            for (let i = 0; i < pages.length; i++) {
                html += '<option value="' + pages[i].pageTypeNameEn + '">' + pages[i].pageTypeName + '</option>'
            }
            pagesHtml = html
            $("#page-page-page").html(html);
        }
    );
    refreshCartGroup()
}

function getNextFile() {
    $.get(
        "http://127.0.0.1:12301/dataFile/getLast", {}, function (data) {
            filePath = data.data;
            buildByFilePath();
        }
    );
}

function buildByFilePath() {
    $.get(
        "http://localhost:12301/read/ppt/filePath",
        //"http://localhost/1.json",
        {file: filePath},
        function (data) {
            ppt = data;
            buildPpt(ppt, index);
        }
    );
}

function buildPpt(data, index) {
    $(".tab-page").html("当前页码: 第" + (index + 1) + "页，共" + data.length + "页 ")
    let html = "";
    for (let i = 0; i < data[index].elements.length; i++) {
        let left = data[index].elements[i].left * cmPx;
        let top = data[index].elements[i].top * cmPx;
        let width = data[index].elements[i].width * cmPx;
        let height = data[index].elements[i].height * cmPx;
        html += '<div id="ppt' + (i + 1) + '" class="ppt-element" style="width: ' + width + 'px;height: '
            + height + 'px;left: ' + left + 'px;top: ' + top + 'px; background: none;z-index:1;' +
            'position: absolute;border-color: ' + data[index].elements[i].color +
            ';border-width: medium; border-style: solid;">' + data[index].elements[i].text + '</div>';
    }
    let pptMain = $(".ppt-main");
    pptMain.css("background", "url('" + ppt[index].img + "')")
    pptMain.html(html);
    groupIds = []
    thisPageDelete = []
    nowChooseId = null
    groups = [];
    groupElements = []
    page = {}
    refreshCartDelete()
    refreshCartGroup()
}

function lastPage() {
    if (index === 0) {
        alert("已经在第一页!")
        return;
    }
    index--;
    buildPpt(ppt, index);
}

function nextPage() {
    if (index === ppt.length - 1) {
        alert("已经最后一页!")
        return;
    }
    index++;
    buildPpt(ppt, index);
}

function refreshPage() {
    nextPage()
    lastPage()
}

function abandonPage() {
    $.get(
        "http://localhost:12301/dataFile/abandonPPT", {
            "path": filePath
        }, function (data) {
            refreshPage()
        }
    );
}

function showElementBtn(id) {
    nowChooseId = id;
    $(".element-text-show").html('当前选中: #<span>' + id + '</span> ' + $("#" + id).html())
}

function deleteElement() {
    let id = getRealId(nowChooseId);
    if (id == null) {
        alert("组合元素不可以直接删除！")
        return;
    }
    thisPageDelete.push(id)
    $("#" + nowChooseId).hide()
    refreshCartDelete()
}

function cartClose() {
    $(".cart-mark").css("display", "none")
}

function cartShow() {
    $(".cart-mark").css("display", "block")
}

function groupSave() {
    let elementInGroup = []
    for (let i = 0; i < groupIds.length; i++) {
        let realId = getRealId(groupIds[i])
        if (realId != null) {
            ppt[index].elements[realId].id = realId
            elementInGroup.push(ppt[index].elements[realId])
        } else {
            let groupId = getRealGroupId(groupIds[i])
            elementInGroup.push({
                id: groupId,
                type: TYPE_GROUP
            })
        }
    }
    groups.push(elementInGroup)
    let groupElement = buildGroupElement(groupIds, groups.length)
    groupElements.push(groupElement)
    groupIds = []
    refreshCartGroup()
    groupClose()
}

function refreshCartGroup() {
    let html = ''
    for (let i = 0; i < groups.length; i++) {
        html += '<div class="cart-list-once">' + buildGroupHtml(groups[i], LIST_GROUP + "-" + i + "-") + '</div>'
    }
    html += '<div class="group-save-btn" onclick="savePage()">保存本页</div>'
    $('.cart-list').html(html)
    chooseSelected()
}


function chooseSelected() {
    for (let i = 0; i < groups.length; i++) {
        for (let j = 0; j < groups[i].length; j++) {
            let thisElements = groups[i][j]
            let name = LIST_GROUP + "-" + i + "-" + j;
            if (!thisElements.hasOwnProperty("theme")) {
                if (thisElements.type === TYPE_TEXT) {
                    if (maybeYear(thisElements.text)) {
                        thisElements.theme = TEXT_TYPE_YEAR
                    } else if (maybeNumber(thisElements.text)) {
                        thisElements.theme = TEXT_TYPE_NUMBER
                    } else if (thisElements.text.length > 15) {
                        thisElements.theme = TEXT_TYPE_LIPSUM
                    } else {
                        thisElements.theme = TEXT_TYPE_BIG_TITLE
                    }
                }
            }
            $("#" + name + "-theme").val(thisElements.theme);

            if (!thisElements.hasOwnProperty("shelter")) {
                thisElements.shelter = NONE
            }
            $("#" + name + "-shelter").val(thisElements.shelter);
        }
    }
    for (let i = 0; i < groupElements.length; i++) {
        let group = groupElements[i]
        if (!group.hasOwnProperty("style")) {
            group.style = NONE;
        }
        $("#" + LIST_GROUP + "-" + i + "-main-style").val(group.style);
        if (!group.hasOwnProperty("purpose")) {
            group.purpose = page.page;
        }
        let purposeName = LIST_GROUP + "-" + i + "-main-purpose";
        $("#" + purposeName).val(group.purpose);
        chooseSelect(purposeName)
        $("#" + LIST_GROUP + "-" + i + "-main-element").val(group.element);
        $("#" + LIST_GROUP + "-" + i + "-main-transverse").val(group.transverse);
        $("#" + LIST_GROUP + "-" + i + "-main-portrait").val(group.portrait);
        $("#" + LIST_GROUP + "-" + i + "-main-alignment").val(group.alignment);
        $("#" + LIST_GROUP + "-" + i + "-main-alignmentGroup").val(group.alignmentGroup);
    }
    $("#page-page-page").val(page.page);
    $("#page-page-style").val(page.style);
    $("#page-page-special").val(page.special);
    $("#page-page-background").val(page.background);
}

function savePage() {
    $.ajax({
        type: "POST",
        url: "http://127.0.0.1:12301/template/build/page",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "groups": groups,
            "groupElements": groupElements,
            "page": page,
            "file": filePath,
            "pageIndex": index
        }),
        success: function () {
            cartClose()
            nextPage()
        }
    });
}

function getElementByGroupId(groupIds, i) {
    let realId = getRealId(groupIds[i]);
    if (realId != null) {
        return ppt[index].elements[realId];
    } else {
        return groupElements[getRealGroupId(groupIds[i])];
    }
}

function buildGroupSize(groupIds, hideElement = false) {
    let groupElement = {};
    groupElement.left = 9999;
    groupElement.top = 9999;
    groupElement.right = 0;
    groupElement.bottom = 0;
    for (let i = 0; i < groupIds.length; i++) {
        let element = getElementByGroupId(groupIds, i);
        groupElement.left = Math.min(groupElement.left, element.left)
        groupElement.top = Math.min(groupElement.top, element.top)
        groupElement.right = Math.max(groupElement.right, element.left + element.width)
        groupElement.bottom = Math.max(groupElement.bottom, element.top + element.height)
        if (hideElement) {
            $("#" + groupIds[i]).hide()
        }
    }
    return groupElement;
}

function buildGroupElement(groupIds, id) {
    let main = $(".ppt-main");
    let groupElement = buildGroupSize(groupIds, true);
    let html = main.html() + '<div id="pptGroup' + id + '" class="ppt-element" style="width: ' +
        (groupElement.right - groupElement.left) * cmPx + 'px;height: '
        + (groupElement.bottom - groupElement.top) * cmPx + 'px;left: ' + groupElement.left * cmPx + 'px;top: ' + groupElement.top * cmPx +
        'px; background: none;z-index:1;' + 'position: absolute;border-color:#a80000;border-width: medium; border-style: solid;">合成图案</div>'
    main.html(html)
    groupElement.type = TYPE_GROUP
    groupElement.width = groupElement.right - groupElement.left
    groupElement.height = groupElement.bottom - groupElement.top
    groupElement.transverse = 'center'
    groupElement.portrait = 'center'
    return groupElement;
}

function chooseSelect(name) {
    let choose = $("#" + name).val();
    let strArr = name.split("-")
    if (strArr[2] === 'main') {
        groupElements[strArr[1]][strArr[3]] = choose
        if (strArr[3] === 'purpose') {
            let elementIdName = name.replace("purpose", "element")
            let html = ''
            for (let i = 0; i < pages.length; i++) {
                if (pages[i].pageTypeNameEn === choose) {
                    for (let j = 0; j < pages[i].models.length; j++) {
                        html += '<option value="' + pages[i].models[j].id + '">'
                            + pages[i].models[j].pptPageModelName + '</option>'
                    }
                }
            }
            $("#" + elementIdName).html(html + '<option value="page">整页（选此不填其他）</option>')
        }
    } else if (strArr[1] === 'page') {
        page[strArr[2]] = choose
    } else {
        groups[strArr[1]][strArr[2]][strArr[3]] = choose
    }
    console.log(groups)
    console.log(groupElements)
    console.log(page)
}

function groupClose() {
    $(".group-save-mark").css("display", "none")
}

function saveGroupWithFind() {
    saveGroupWithType($("#saveTabPage").val(), $("#saveTabElementList").val());
}

function saveGroupWithType(purpose, element) {
    groupSave()
    groupElements[groupElements.length - 1].purpose = purpose
    groupElements[groupElements.length - 1].element = element
    refreshCartGroup()
    $(".save-tab").css("display", "none")
}

function saveGroup() {
    groupSave()
    //cartShow()
    $(".save-tab").css("display", "none")
    $(".cart-list-once:eq(" + (groups.length) + ")").css("background-color", "#858585")
}

function buildPageElementSelect(selectName) {
    return '<div class="group-label-box"> <label> <span>页面</span> ' +
        '<select onclick=chooseSelect("' + selectName + 'main-purpose") id="' + selectName + 'main-purpose">' +
        pagesHtml +
        '</select>' +
        '</label> </div><div class="group-label-box"> <label> <span>元素</span> ' +
        '<select onclick=chooseSelect("' + selectName + 'main-element") id="' + selectName + 'main-element">' +
        '<option value=""></option>' +
        '</select>' +
        '</label> </div> ';
}

function buildGroupHtml(groups, selectName) {
    let html =
        '<div class="cart-list-group-this">' +
        '<div class="group-element">【整体标注】</div>' +
        '<div class="sign-box">' +
        buildPageElementSelect(selectName) +
        '<div class="group-label-box"> <label> <span>横向对齐</span> ' +
        '<select onclick=chooseSelect("' + selectName + 'main-transverse") id="' + selectName + 'main-transverse">' +
        '<option value=""></option>' +
        '<option value="left">左对齐</option>' +
        '<option value="center">居中</option>' +
        '<option value="right">右对齐</option>' +
        '</select>' +
        '</label> </div> <div class="group-label-box"> <label> <span>纵向对齐</span>' +
        '<select onclick=chooseSelect("' + selectName + 'main-portrait") id="' + selectName + 'main-portrait">' +
        '<option value=""></option>' +
        '<option value="top">上对齐</option>' +
        '<option value="center">居中</option>' +
        '<option value="bottom">下对齐</option>' +
        '</select>' +
        '</label> </div> <div class="group-label-box"> <label> <span>组间方位</span>' +
        '<select onclick=chooseSelect("' + selectName + 'main-alignment") id="' + selectName + 'main-alignment">' +
        '<option value="">无方位</option>' +
        '<option value="left">文字靠左</option>' +
        '<option value="right">文字靠右</option>' +
        '</select>' +
        '</label> </div> <div class="group-label-box"> <label> <span>关联其他组</span>' +
        '<select onclick=chooseSelect("' + selectName + 'main-alignmentGroup") id="' + selectName + 'main-alignmentGroup">' +
        '<option value="">无方位无需关联</option>' +
        '<option value="1">1</option>' +
        '<option value="2">2</option>' +
        '<option value="3">3</option>' +
        '<option value="4">4</option>' +
        '<option value="5">5</option>' +
        '<option value="6">6</option>' +
        '<option value="7">7</option>' +
        '<option value="8">8</option>' +
        '<option value="9">9</option>' +
        '<option value="10">10</option>' +
        '</select>' +
        '</label> </div> </div> </div>'
    for (let i = 0; i < groups.length; i++) {
        if (groups[i].type === TYPE_GROUP) {
            continue;
        }
        let element = groups[i];
        if (element.type === TYPE_IMG) {
            html += '<div class="cart-list-group-img"><div class="group-element">#' + element.id + ' : ' + element.text +
                '</div><div class="sign-box"><div class="group-label-box"><label><span>内容</span>' +
                '<select onclick=chooseSelect("' + selectName + i + '-theme") id="' + selectName + i + '-theme">' +
                '<option value=""></option>' +
                '<option value="scenery">建筑或风景</option>' +
                '<option value="build">办公场景</option>' +
                '<option value="icon">图标</option>' +
                '<option value="logo">LOGO</option>' +
                '<option value="shelter">遮挡物</option>' +
                '</select>' +
                '</label></div><div class="group-label-box"><label><span>形式</span>' +
                '<select onclick=chooseSelect("' + selectName + i + '-shelter") id="' + selectName + i + '-shelter">' +
                '<option value=""></option>' +
                '<option value="none">无需渐变</option>' +
                '<option value="left">主体在左侧</option>' +
                '<option value="right">主体在右侧</option>' +
                '</select></label></div></div></div>'
        }
    }
    return html;
}

function cartListShow() {
    $("#cart-list").attr("class", "cart-tab-once cart-tab-hot");
    $("#cart-delete").attr("class", "cart-tab-once");
    $(".cart-del").css("display", "none")
    $(".cart-list").css("display", "block")
}

function cartDeleteShow() {
    $("#cart-list").attr("class", "cart-tab-once");
    $("#cart-delete").attr("class", "cart-tab-once cart-tab-hot");
    $(".cart-list").css("display", "none")
    $(".cart-del").css("display", "block")
}

function refreshCartDelete() {
    let html = "";
    for (let i = 0; i < thisPageDelete.length; i++) {
        let element = ppt[index].elements[thisPageDelete[i]];
        html += '<div class="cart-del-once"><div class="cart-del-title">' + element.text +
            '(Left: ' + element.left + ' Width: ' + element.width + ')' + '</div>' +
            '<div class="cart-del-recovery">恢复</div></div>'
    }
    $(".cart-del").html(html)
}

function getRealId(idName) {
    let id = idName.replace("ppt", "") - 1
    if (!isNumber(id)) {
        return null;
    }
    return id;
}

function isNumber(val) {
    const regPos = /^[0-9]+.?[0-9]*/;
    return regPos.test(val);
}

function nonNull(str) {
    return str != null && str.length > 0
}

function getRealGroupId(idName) {
    return idName.replace("pptGroup", "") - 1
}

function maybeYear(str) {
    return startWith(str, "20") && str.length < 6
}

function maybeNumber(str) {
    str = str.toLowerCase().replace("part", "").replace(" ", "");
    return !!(startWith(str, "0") || startWith(str, "1")
        || startWith(str, "2") || startWith(str, "3")
        || startWith(str, "4") || startWith(str, "5")
        || startWith(str, "6") || startWith(str, "7")
        || startWith(str, "8") || startWith(str, "9"));
}

function startWith(str1, headStr) {
    if (str1.substr(0, headStr.length) === headStr) {
        console.log(true);
    }
}
