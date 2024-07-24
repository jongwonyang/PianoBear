<template>
    <div class="regist-container">
        <div class="regist-box">
            <md-elevation></md-elevation>
            <div class="regist-text">회원가입</div>
            <div class="input-box">
                <md-outlined-text-field label="아이디" type="text" placeholder="영문+숫자 4~20자" class="regist-input id"></md-outlined-text-field>
                <md-outlined-text-field label="이메일" type="email" placeholder="abc@example.com" class="regist-input email"></md-outlined-text-field>
                <md-outlined-text-field label="이름" type="text" class="regist-input name"></md-outlined-text-field>
                <md-outlined-text-field label="비밀번호" type="password" placeholder="영문+숫자 8~20자" class="regist-input pw"></md-outlined-text-field>
                <md-outlined-text-field label="비밀번호 확인" type="password"
                    class="regist-input pwCheck"></md-outlined-text-field>
                <md-outlined-select label="성별" class="select-box">
                    <md-select-option value="남">
                        <div slot="headline">남</div>
                    </md-select-option>
                    <md-select-option value="여">
                        <div slot="headline">여</div>
                    </md-select-option>
                </md-outlined-select>
                <div class="birth-box">
                    <md-outlined-text-field label="생년월일" type="date" class="regist-input birth"></md-outlined-text-field>
                </div>
            </div>
            <md-elevated-button class="regist-button">회원가입</md-elevated-button>
        </div>
    </div>
</template>
<script setup>

</script>

<style scoped>
.regist-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 100%;
}

.regist-box {
    background: #FFF9E0;
    position: relative;
    padding: 60px 40px;
    border-radius: 30px;
    text-align: center;
    max-width: 400px;
    width: 100%;

}

.regist-text {
    padding: 10px;
    font-size: 24px;
    font-weight: 500;
    margin-bottom: 20px;
    color: #947650;
}

.regist-input {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #FBFCFE;
    border-radius: 10px;
    font-size: 16px;
    --md-outlined-text-field-input-text-placeholder-color: #a5a5a5;
}

.regist-button {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #947650;
    border-radius: 10px;
    font-size: 16px;
}

.select-box {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #FBFCFE;
    border-radius: 10px;
    font-size: 16px;
}
</style>