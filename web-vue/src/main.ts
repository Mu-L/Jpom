///
/// Copyright (c) 2019 Of Him Code Technology Studio
/// Jpom is licensed under Mulan PSL v2.
/// You can use this software according to the terms and conditions of the Mulan PSL v2.
/// You may obtain a copy of Mulan PSL v2 at:
/// 			http://license.coscl.org.cn/MulanPSL2
/// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
/// See the Mulan PSL v2 for more details.
///

import '@/assets/style.less'
import '@/assets/reset.less'
import App from './App.vue'
import router from './router'
import '@/router/auth'
import i18n from './i18n'
import { changeLang, defaultLocale } from './i18n'

changeLang(defaultLocale).then(() => {
  //console.log('defaultLocale done', new Date().getTime())
  const pinia = createPinia()

  const app = createApp(App)

  app.use(router)
  app.use(pinia)
  app.use(i18n)

  app.mount('#app')
})
//console.log('app done', new Date().getTime())
