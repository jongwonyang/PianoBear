<template>

    <div class="reset-container">
        <div class="reset-box">
            <md-elevation></md-elevation>
            <div class="reset-text">비밀번호 재설정</div>
            <div class="input-box">
                <md-outlined-text-field label="아이디" type="text" class="reset-input id"></md-outlined-text-field>
                <md-outlined-text-field label="이름" type="text" class="reset-input name"></md-outlined-text-field>
                <md-outlined-text-field label="이메일" type="email" class="reset-input email"></md-outlined-text-field>
            </div>
            <md-elevated-button class="reset-button">임시 비밀번호 발급</md-elevated-button>
        </div>
    </div>

</template>
<script setup>

</script>

<style scoped>

.reset-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 100%;
}

.reset-box {
    background: #FFF9E0;
    position: relative;
    padding: 60px 40px;
    border-radius: 30px;
    text-align: center;
    max-width: 400px;
    width: 100%;

}

.reset-text {
    padding: 10px;
    font-size: 24px;
    font-weight: 500;
    margin-bottom: 20px;
    color: #947650;
}


.reset-input {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #FBFCFE;
    border-radius: 10px;
    font-size: 16px;
}

.reset-button {
    margin-top: 20px;
    background-color: #947650;
    color: #FFF9E0;
    border-radius: 10px;
    font-size: 16px;
}
</style>