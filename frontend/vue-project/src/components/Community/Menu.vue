<template>
    <div>
        <span style="position: relative">
            <md-filled-icon-button id="usage-anchor" @click="menuOpen">
                <md-icon>more_horiz</md-icon>
            </md-filled-icon-button>
            <md-menu id="usage-menu" anchor="usage-anchor" :ref="props.next">
                <md-menu-item>
                <div slot="headline">음량조절</div>
                </md-menu-item>
                <md-menu-item>
                <div slot="headline">음소거</div>
                </md-menu-item>
                <md-menu-item>
                <div slot="headline">친구추가</div>
                </md-menu-item>
            </md-menu>
        </span>
    </div>
</template>
<script lang="ts" setup>
    import {ref, defineProps} from 'vue';

    const props = defineProps({
        next: String
    });

    interface Element {
        open : boolean;
    }
    const arr = ref(['a1', 'a2', 'a3', 'a4', 'a5', 'a6']);
    const a1 = ref<Element | null>(null);

</script>

<style scoped>
</style>