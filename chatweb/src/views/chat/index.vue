<template>
    <div>
        <div class="pt-r" style="min-height:95vh;">
            <div class="search-head">
                <div class="search-div">
                    <div class="new-container header-container">
                        <div class="">
                            <div class="head-div m-l-20 f-l" style="width: 240px;"> 
                                <div class="model-select-div" @click="openModSelectBox">{{modeName}}<span class="icon-angle-down model-type-btn"></span></div>
                            </div>
                            <div v-if="showInput">
                                <div class="user-div f-r d-f" style="margin-right: 10px;">
                                    <div>
                                        <input type="text" v-model="userName" class="user-input" 
                                                ref="userNameRef" placeholder="用户名，用于保存会话" @blur="freshTitles"/>
                                    </div>
                                    <div class="info-btn" @mouseenter="showUserNameTitle()"
                                                            @mouseleave="hideUserNameTitle()" >
                                        <span class="icon-info-4"></span>
                                    </div>
                                </div>
                                <div class="user-div f-r d-f">
                                    <div>
                                        <input type="text" v-model="apiKey" class="user-input" ref="apiKeyRef" placeholder="ApiKey" style="width: 375px;"/>
                                    </div>
                                    <div class="info-btn" @mouseenter="showApikeyTitle()"
                                                            @mouseleave="hideApikeyTitle()">
                                        <span class="icon-info-4"></span>
                                    </div>
                                </div>
                            </div>
                            <div class="f-r">
                                <div v-if="showInput" @click="showInput = !showInput" class="show-hide-input">
                                    <span class="icon-right-open-3"></span>
                                </div>
                                <div v-if="!showInput" @click="showInput = !showInput" class="show-hide-input">
                                    <span class="icon-left-open-3"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div style="display: flex;">
                <div :style="{width:leftMenuWidth}" id="leftOutBox">
                    <div class="left-menu-box" :style="{width: leftMenuWidth}" id="leftInBox">
                        <div class="icon-th-list-2 menu-btn" @click="menuHide"></div>
                        <div v-show="menuHideFlag" class="icon-feather menu-btn-1" @click="newChat"></div>
                        <div v-show="!menuHideFlag">
                            <div class="div-icon-new icon-feather new-chat-btn" @click="newChat">
                                &nbsp;&nbsp;&nbsp;&nbsp;发起新的对话</div>
                            <div class="recently-chats"> 近期对话<span class="icon-ccw chat-refresh-btn" @click="freshChatTitle()"></span></div>
                            <div v-for="(title, index) in chatTitles" class="chat-title-name"
                                :class="{ 'chat-title-name-selected': selectedTitleId === index }" :key="index"
                                :ref="'aaa' + index" 
                                @mouseenter="showThisOper(index)"
                                @mouseleave="hideThisOper(index)"
                            >
                                <div class="f-l chat-title-div" :title="title.titleName"
                                    @click="getChatInfo(title.chatId, index, title.modelName)"
                                    v-if="title.showName">
                                    <span>{{ title.titleName }}</span>
                                </div>
                                <div class="f-l chat-title-div" 
                                     v-if="!title.showName">
                                    <span><input type="text" v-model="title.titleName" class="chat-title-input"/></span>
                                </div>
                                <div v-show="title.showOper" class="f-r chat-oper" @click="delTitle(title.chatId)"><span class="icon-trash model-type-btn-2"></span></div>
                                <div v-show="title.showOper" v-if="title.showName" class="f-r chat-oper" @click="editTitle(title.chatId, index)"><span class="icon-edit model-type-btn-2"></span></div>
                                <div v-show="title.showOper" v-if="!title.showName" class="f-r chat-oper" @click="saveChatTitle(title.chatId, index)"><span class="icon-ok-2 model-type-btn-2"></span></div>
                            </div>
                            <div class="chat-title-name" v-if="isLogin === false">{{ needLoginInfo }}</div>
                        </div>
                    </div>
                </div>
                <div class="area-container new-container chat-div-box" ref="messageContainer" id="chatDiv">
                    <div v-for="(chat, index) in allChats" class="chat-box-out">
                        <div class="user-chat">
                            {{ chat.userChat }}
                        </div>
                        <div class="ai-chat-title">* {{ modeName }}</div>
                        <div class="ai-chat-info" v-if="chat.aiChat != null">
                            <div v-html="chat.aiChat"></div>
                        </div>
                        <div class="ai-chat-info" v-if="chat.aiChat == null">
                            <img src="@/assets/loading-1.gif" style="width:20px;" alt="" />
                        </div>
                    </div>
                    <div v-if="allChats.length == 0" class="flag-box">
                        Wecode
                    </div>

                    <div style="position: fixed; bottom:0; z-index:9999;">
                        <div class="area-container new-container" style="background-color: #17191a;">
                            <div class="d-f search-area width-new">
                                <div>
                                    <textarea class="textarea-class search-input-style jq_watermark"
                                        v-model="userChatInfo" id="textareaId" ref="chatTextArea" @keydown="keyLsn">
                                    </textarea>
                                </div>
                                <div>
                                    <button type="button" class="search-btn-new search-btn-style" @click="startChat()">
                                        <span class="icon-search"></span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="showModBox" class="model-box b-r-5" style="border-color:rgb(55, 60, 63);background-color:rgb(23, 25, 26);">
                <div v-for="(mod, index) in models" class="model-select" @click="selectMod(index)">
                    {{ mod }}
                    <span v-if="index == selectedModIndex" class="icon-ok-2 model-type-btn-1"></span>
                </div>
            </div>
            <div v-if="showUserTitleBox" class="user-info-box b-r-5" 
                    @mouseenter="showUserNameTitle()"
                    @mouseleave="hideUserNameTitle()"
                    style="border-color:rgb(55, 60, 63);background-color:rgb(23, 25, 26);">
                用户名：必填内容。用于保存对话，可输入任意字符，但如果想查看历史记录（近期对话）就需要输入同一个用户名，例如，可以每次都用tom。在项目中可使用登录后的用户信息代替。
            </div>
            <div v-if="showApikeyTitleBox" class="apikey-box b-r-5" 
                    @mouseenter="showApikeyTitle()"
                    @mouseleave="hideApikeyTitle()"
                    style="border-color:rgb(55, 60, 63);background-color:rgb(23, 25, 26);">
                Apikey：必填内容。可通过 <a style="color:lightblue" href="https://aistudio.google.com/api-keys" target="_blank">https://aistudio.google.com/api-keys</a> 申请。
            </div>
        </div>
    </div>
</template>

<script>
import { getChatRes, getChatInfoList, getChatTitleList, chatWithHistory, newChatRes, deleteChat, saveTitle } from '@/api/chat/index'
import VueMarkdown from 'markdown-it';
import { ref, computed, onMounted } from 'vue';

export default {
    name: "Chat",
    components: { VueMarkdown },
    setup() {
        const userNameRef = ref('')
        const apiKeyRef = ref('')
        // 聚焦函数
        const focusUserInput = () => {
            userNameRef.value.focus()
        }
        const focusApiKeyInput = () => {
            apiKeyRef.value.focus()
        }
        
        return {
            userNameRef,
            apiKeyRef,
            focusUserInput,
            focusApiKeyInput
        }
    },
    data() {
        return {
            selectedTitleId: -1,
            chatTitleClass: 'chat-title-name',
            allChats: [],
            userChatInfo: '',
            chatTitles: [],
            couldChat: true,
            selectedHistory: false,
            selectedChatId: null,
            needLoginInfo: '<输入用户名后点击刷新>',
            isLogin: false,
            isNewChat: false,
            models: [
                "gemini-2.5-flash",
                "gemini-2.5-pro"
            ],
            selectedModIndex: 0,
            showModBox: false,
            showUserTitleBox: false,
            showApikeyTitleBox: false,
            showInput: true,
            modeName: '',
            userName: '张三',
            apiKey: '',
            leftMenuWidth: '260px',
            menuHideFlag: false,
        }
    },
    created() {
        this.modeName = this.models[0]
    },
    methods: {
        delTitle(chatId) {
            const isConfirmed = window.confirm("确认要删除吗？");
            if (isConfirmed) {
                deleteChat(chatId).then(res => {
                    let tempTitles = []
                    this.chatTitles.forEach((tt, index) => {
                        if (tt.chatId != chatId) {
                            tempTitles.push(tt)
                        } else if (this.selectedChatId == chatId) {
                            this.selectedChatId = null
                            this.selectedTitleId = -1
                            this.allChats = []
                        }
                    })
                    this.chatTitles = tempTitles
                })
            } 
        },
        editTitle(chatId, index) {
            this.chatTitles[index].showName = false
        },
        saveChatTitle(chatId, index) {
            const params = {
                chatId : chatId,
                chatText : this.chatTitles[index].titleName
            }
            saveTitle(params).then(res => {
                this.chatTitles[index].showName = true
            })
        },
        freshChatTitle() {
            this.initChatData()
        },
        freshTitles() {
            this.freshChatTitle()
        },
        openModSelectBox() {
            console.log(this.showModBox)
            this.showModBox = !this.showModBox
        },
        selectMod(index) {
            if (this.selectedModIndex != index) {
                this.selectedModIndex = index
                this.modeName = this.models[index]
                // 创建新会话
                this.isNewChat = false
                this.newChat()
            }
            this.showModBox = false
        },
        showThisOper(index) {
            this.chatTitles[index].showOper = true
        },
        hideThisOper(index) {
            this.chatTitles[index].showOper = false
        },
        showUserNameTitle() {
            this.showUserTitleBox = true
        },
        hideUserNameTitle() {
            this.showUserTitleBox = false
        },
        showApikeyTitle() {
            this.showApikeyTitleBox = true
        },
        hideApikeyTitle() {
            this.showApikeyTitleBox = false
        },
        initChatData() {
            if (this.userName) {
                this.chatTitles = []
                getChatTitleList(this.userName).then(res => {
                    if (res.data.code != 200) {
                        this.isLogin = false
                    } else {
                        let titleList = res.data.data
                        if (titleList != null && titleList.length > 0) {
                            titleList.forEach(tt => {
                                tt.showOper = false
                                tt.showName = true
                            })
                            this.chatTitles = titleList
                        }
                    }
                })
                this.isLogin = true
            } else {
                this.focusUserInput()
            }
        },
        getChatInfo(chatId, index, modName) {
            if (this.selectedTitleId === index) {
                return
            }
            this.selectedTitleId = index
            this.allChats = []
            getChatInfoList(chatId).then(res => {
                let chatList = res.data.data
                chatList.forEach(chatInfo => {
                    let chat = {
                        "userChat": chatInfo.userContent,
                        "aiChat": this.compiledMarkdown(chatInfo.chatContent)
                    }
                    this.allChats.push(chat)
                })
                this.scrollToBottom()
                this.selectedHistory = true
                this.selectedChatId = chatId
            })
            this.models.forEach((mod, idx) => {
                if (mod === modName) {
                    this.selectedModIndex = idx
                    this.modeName = mod
                }
            })
        },
        compiledMarkdown(str) {
            return this.$mdParser.render(str)
        },
        keyLsn(e) {
            if (e.keyCode === 13 && e.shiftKey) {
                // shift+回车，换行
            } else if (e.keyCode === 13 ) {
                // 回车，阻止换行，改为发送
                e.preventDefault()
                this.startChat()
            }
        },
        startChat() {
            if (!this.couldChat) {
                return;
            }
            if (!this.apiKey) {
                this.focusApiKeyInput()
            }
            const thisUserChat = this.userChatInfo
            if (thisUserChat !== '' && thisUserChat != null) {
                let thisChat = {
                    "userChat": thisUserChat,
                    "aiChat": null,
                }
                this.allChats.push(thisChat)
                this.couldChat = false
                this.userChatInfo = null
                this.scrollToBottom();
                if (this.selectedTitleId == -1) {
                    this.isNewChat = true
                }
                // 是否选择了某个聊天历史继续聊天
                if (this.selectedHistory) {
                    const params = {
                        userName : this.userName,
                        isNew : false,
                        chatModel : this.modeName,
                        chatId : this.selectedChatId,
                        apiKey : this.apiKey,
                        chatText : thisUserChat,
                        repText : "",
                        isHistory : true
                    }
                    chatWithHistory(params).then(res => {
                        this.handleChatRes(res.data.data)
                    })
                    this.selectedHistory = false // 历史聊天变成当前聊天
                    this.selectedChatId = null
                } else {
                    if (this.isNewChat) {
                        const params = {
                            userName : this.userName,
                            isNew : true,
                            chatModel : this.modeName,
                            chatId : "",
                            apiKey : this.apiKey,
                            chatText : thisUserChat,
                            repText : "",
                            isHistory : false
                        }
                        newChatRes(params).then(res => {
                            if (res.data.code != 200) {
                                this.handleChatRes(res.data.data)
                            } else {
                                this.handleChatRes(res.data.data.repText)
                                this.isNewChat = false
                                let chatTitleTemp = []
                                let newChatTitle = {
                                    chatId: res.data.data.chatId,
                                    titleName: thisUserChat,
                                    showOper: false,
                                    showName: true
                                }
                                chatTitleTemp.push(newChatTitle)
                                this.chatTitles.forEach(title => {
                                    chatTitleTemp.push(title)
                                })
                                this.chatTitles = chatTitleTemp
                                this.selectedTitleId = 0
                                this.isLogin = true
                            }
                        })
                    } else {
                        const params = {
                            userName : this.userName,
                            isNew : false,
                            chatModel : this.modeName,
                            chatId : "",
                            apiKey : this.apiKey,
                            chatText : thisUserChat,
                            repText : "",
                            isHistory : false
                        }
                        getChatRes(params).then(res => {
                            this.handleChatRes(res.data.data)
                        })
                    }
                }
                this.scrollToBottom();
            } else {

            }
        },
        handleChatRes(resData) {
            if (resData != null && resData != '') {
                let replaceResData = this.compiledMarkdown(resData)
                this.allChats[this.allChats.length - 1].aiChat = replaceResData
            }
            this.couldChat = true // 可以继续聊天
        },
        // 创建新对话
        newChat() {
            if (this.isNewChat) {
                return
            }
            this.allChats = []
            this.selectedTitleId = -1
            this.selectedHistory = false
            this.isNewChat = true
            this.$refs.chatTextArea.focus();
        },
        scrollToBottom() {
            this.$nextTick(() => {
                const newElement = this.$refs.messageContainer
                if (newElement) {
                    const eles = newElement.querySelectorAll('.chat-box-out')
                    if (eles.length > 0) {
                        eles[eles.length - 1].scrollIntoView({
                            behavior: 'smooth', // 平滑滚动
                            block: 'start'      // 将元素顶部对齐到滚动容器的顶部
                        });
                    }
                }
            });
        },
        menuHide() {
            if (!this.menuHideFlag) {
                this.menuHideFlag = true
                this.leftMenuWidth = '56px'
            } else {
                this.menuHideFlag = false
                this.leftMenuWidth = '260px'
            }
        },
    },
    mounted: function() {
        try {
            initWaterMark();
        } catch(e) {

        }
    }
}
</script>

<style>
@import 'https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/4.0.0/github-markdown.min.css';

.mark-icon {
    width: 120px;
}

.textarea-class {
    height: 70px;
    width: 800px;
    color: #fff !important;
    text-shadow: none;
    outline-style: none;
    font-size: 16px;
    border-top-color: rgb(59, 65, 67);
    border-right-color: rgb(59, 65, 67);
    border-left-color: rgb(59, 65, 67);
    border-bottom-color: rgb(59, 65, 67);
    border-radius: 5px;
    -webkit-border-radius: 5px;
    -moz-border-radius: 5px;
    -webkit-box-shadow: none;
    box-shadow: none;
    padding: 10px 10px 10px 15px;
    outline-style: none;
    resize: none;
    overflow-y: hidden;
}

.width-new {
    max-width: 800px !important;
}

.search-btn-new {
    margin-left: -53px;
    position: absolute;
    z-index: 99999;
    bottom: 7px;
    background: #fff;
    width: 50px;
    height: 36px;
    font-size: 16px;
    border: 0;
    outline: 0;
    padding: 0;
    border-radius: 0 5px 5px 0;
    -webkit-border-radius: 0 5px 5px 0;
    -moz-border-radius: 0 5px 5px 0;
}

.user-chat {
    font-size: 16px;
    color: #ffffff;
    text-align: justify;
    background-color: #333436;
    padding: 12px 18px;
    border-radius: 12px;
    margin: 30px 0 30px 320px;
    line-height: 1.75rem;
}

.ai-chat-title {
    font-size: 14px;
    color: #c5c4c4;
    margin: 40px 0 10px 0;
    font-weight: bold;
}

.ai-chat-info {
    font-size: 16px;
    color: #ffffff;
    text-align: justify;
    margin: 10px 0;
    line-height: 1.9rem;
}

.ai-chat-info p,
.ai-chat-info li {
    margin-bottom: 10px;
}

.ai-chat-info ul {
    list-style: inside;
    padding-left: 20px;
}

.ai-chat-info ol {
    padding-left: 20px;
}

.ai-chat-info hr {
    border-bottom: #79746c;
}

.ai-chat-info h3 {
    color: #b5b5b5;
    font-size: 16px;
}
.ai-chat-info h4 {
    color: #b8b8b8;
    font-size: 16px;
    line-height: 1.9rem;
}
.ai-chat-info a {
    color: #337ab7 !important;
}

.left-menu-box {
    background: #292929;
    position: fixed;
    left: 0;
    top: 58px;
    min-height: 94.6vh;
}

.menu-btn {
    font-size: 24px;
    color: #b7b3b3;
    margin: 10px 20px;
    cursor: pointer;
}
.menu-btn-1 {
    font-size: 24px;
    color: #b7b3b3;
    margin: 20px 10px;
    padding: 10px;
    cursor: pointer;
}

.div-icon-new {
    cursor: pointer;
}

.new-chat-btn {
    font-size: 14px;
    color: #b7b3b3;
    margin: 30px 20px;
    border: 1px solid;
    padding: 12px 44px;
    border-radius: 10px;
}

.recently-chats {
    font-size: 16px;
    color: #b7b3b3;
    margin-top: 50px;
    margin-left: 20px;
    margin-bottom: 15px;
}

.chat-title-name {
    font-size: 14px;
    color: #fff;
    margin-top: 5px;
    margin-left: 10px;
    cursor: pointer;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 230px;
    padding: 0 10px;
}

.chat-title-name:hover {
    font-size: 14px;
    color: #fff;
    margin-top: 5px;
    margin-left: 10px;
    cursor: pointer;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 230px;
    /* border: 1px solid; */
    padding: 0 10px;
    border-radius: 30px;
    background-color: #373939;
}

.chat-title-name-selected {
    font-size: 14px;
    color: #fff;
    margin-top: 5px;
    margin-left: 10px;
    cursor: pointer;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 230px;
    /* border: 1px solid; */
    padding: 0 10px;
    border-radius: 30px;
    background-color: #16456b !important;
}

.chat-div-box {
    padding-top: 70px;
    padding-bottom: 100px;
    width: 800px;

}

.chat-div-box p code,
.chat-div-box li code {
    background-color: #292a2f;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
    font-size: 14px;
    padding: 0.2em 0.4em;
    margin: 0;
    border-radius: 6px;
    color: #9c9faa;
}

/* 针对 markdown-it 生成的代码块 */
.chat-div-box pre {
    background-color: #292a2f;
    padding: 16px;
    margin-bottom: 15px;
    border-radius: 12px;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
    font-size: 14px;
    line-height: 1.45rem;
    overflow-x: auto;
}

/* 确保代码高亮不会覆盖背景色 */
.chat-div-box pre code {
    background-color: transparent !important;
}

.search-div {
    background-color: rgb(31, 34, 35);
    border-bottom-color: rgb(59, 65, 67);
    box-shadow: rgb(43 47 48) 0px 1px 2px;
}

.search-input-style {
    border-top-color: rgb(59, 65, 67);
    border-bottom-color: rgb(59, 65, 67);
    background-color: #17191a;
    color: rgb(155, 147, 136)
}

.search-btn-style {
    background-color: rgb(23, 25, 26);
    border-right-color: rgb(59, 65, 67);
    border-top-color: rgb(59, 65, 67);
    border-bottom-color: rgb(59, 65, 67);
}
.model-select-div {
    font-size: 18px;
    color: #bcbcbc;
    font-weight: bold;
    cursor: pointer;
    text-align: left;
}
.model-type-btn {
    font-size: 20px;
    color: #b7b3b3;
    margin: 10px;
}
.model-type-btn-1 {
    font-size: 15px;
    color: #b7b3b3;
    margin: 10px;
}
.model-type-btn-2 {
    font-size: 14px;
    color: #b7b3b3;
    margin: 5px;
}
.model-box {
    border: 1px solid #dadada;
    padding: 3px;
    top: 50px;
    left: 18px;
    width: 300px; 
    background-color: #fff;
    -webkit-box-shadow: 1px 1px 8px rgba(0, 0, 0, .12);
    box-shadow: 1px 1px 8px rgba(0, 0, 0, .12);
    z-index: 9999;
    background-color: rgb(54 54 55);
    position:fixed;
}
.user-info-box {
    left: auto;
    right: 10px;
    padding: 10px;
    color: #c6c6c6;
    border: 1px solid #dadada;
    top: 40px;
    width: 300px; 
    background-color: #fff;
    -webkit-box-shadow: 1px 1px 8px rgba(0, 0, 0, .12);
    box-shadow: 1px 1px 8px rgba(0, 0, 0, .12);
    z-index: 9999;
    background-color: rgb(54 54 55);
    position:fixed;
}
.apikey-box {
    left: auto;
    right: 184px;
    padding: 10px;
    color: #c6c6c6;
    border: 1px solid #dadada;
    top: 40px;
    width: 300px; 
    background-color: #fff;
    -webkit-box-shadow: 1px 1px 8px rgba(0, 0, 0, .12);
    box-shadow: 1px 1px 8px rgba(0, 0, 0, .12);
    z-index: 9999;
    background-color: rgb(54 54 55);
    position:fixed;
}
.model-select {
    font-size: 16px;
    color: #c5bdbd;
    padding: 10px 20px;
    cursor: pointer;
}
.model-select:hover {
    color: #fff;
}
.user-input {
    padding: 3px 10px;
    font-size: 14px;
    color: #999999;
    background: #3f3f3f;
    border: 1px solid #333333;
    border-radius: 5px;
}
.user-div {
    margin-top: 15px;
    margin-left: 10px;
}
.chat-refresh-btn {
    font-size: 16px;
    color: #b7b3b3;
    margin: 10px;
    cursor: pointer;
}
.chat-title-div {
    width: 160px;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    padding: 10px 0;
}
.chat-title-input {
    width: 160px;
    color: #1e1e1e;
}
.info-btn {
    padding: 3px 10px;
    margin-left: -33px;
    color: #9c9c9c;
    font-size: 15px;
}
.show-hide-input {
    margin: 14px 0px;
    font-size: 20px;
    padding: 0 10px;
    cursor: pointer;
}
.flag-box {
    height: 86vh;
    align-items: center;
    justify-content: center;
    display: flex;
    font-size: 80px;
    color: #2e2e2e;
}
.chat-oper {
    padding: 10px 0;
}
</style>.