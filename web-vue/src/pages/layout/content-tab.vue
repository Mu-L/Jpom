<template>
  <a-tabs v-model:activeKey="activeTabKey" class="my-tabs" hide-add type="editable-card" @edit="onEdit">
    <a-tab-pane v-for="(tab, index) in tabList" :key="tab.key" :closable="tabList.length > 1">
      <template #tab>
        <a-dropdown :trigger="['contextmenu']">
          <span style="display: inline-table">{{ tab.title }}</span>
          <template #overlay>
            <a-menu>
              <a-menu-item
                @click="
                  closeTabs({
                    key: tab.key
                  })
                "
              >
                <a-button type="link" :disabled="tabList.length <= 1">{{ $t('i18n_6816da19f3') }}</a-button>
              </a-menu-item>
              <a-menu-item
                @click="
                  closeTabs({
                    key: tab.key,
                    position: 'left'
                  })
                "
              >
                <a-button type="link" :disabled="tabList.length <= 1 || index === 0">{{
                  $t('i18n_e9290eaaae')
                }}</a-button>
              </a-menu-item>
              <a-menu-item
                @click="
                  closeTabs({
                    key: tab.key,
                    position: 'right'
                  })
                "
              >
                <a-button type="link" :disabled="tabList.length <= 1 || index === tabList.length - 1"
                  >{{ $t('i18n_649d90ab3c') }}
                </a-button>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
    </a-tab-pane>
    <template #rightExtra> <user-header :mode="props.mode" /> </template>
  </a-tabs>
</template>
<script lang="ts" setup>
import userHeader from './user-header.vue'
import { useAllMenuStore } from '@/stores/menu2'
import { useI18n } from 'vue-i18n'
import { Key } from 'ant-design-vue/es/_util/type'
const { t: $t } = useI18n()
const router = useRouter()
const route = useRoute()

const props = defineProps<{
  mode: string
}>()

const menuStore = useAllMenuStore()
const tabList = computed(() => {
  return menuStore.getTabList(props.mode)
})
const activeTabKey = computed({
  get() {
    return menuStore.getActiveTabKey(props.mode)
  },
  set(value) {
    activeTab(value)
  }
})

const activeTab = (key?: string) => {
  key = key || activeTabKey.value
  const index = tabList.value.findIndex((ele: any) => ele.key === key)
  const activeTab = tabList.value[index]
  router.push({
    query: { ...route.query, sPid: activeTab.parentId, sId: activeTab.id },
    path: activeTab.path
  })

  menuStore.activeMenu(props.mode, activeTab.id)

  return activeTab
}

const onEdit = (key: Key | MouseEvent | KeyboardEvent, action: 'add' | 'remove') => {
  if (action === 'remove') {
    if (tabList.value.length === 1) {
      $notification.warn({
        message: $t('i18n_b953d1a8f1')
      })
      return
    }
    menuStore.removeTab(props.mode, key).then(() => {
      activeTab()
    })
  }
}

// 关闭 tabs
const closeTabs = (data: any) => {
  $notification.success({
    message: $t('i18n_33130f5c46')
  })
  menuStore.clearTabs(props.mode, data).then(() => {
    activeTab()
  })
}
</script>
<style scoped>
.my-tabs {
  width: 50vw;
  /* flex: auto; */
  /* align-self: center; */
  height: 40px;
}
</style>
