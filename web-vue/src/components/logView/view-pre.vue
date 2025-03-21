<template>
  <div class="log-view-wrapper force-scrollbar">
    <RecycleScroller
      :id="uniqueId"
      class="scroller"
      :style="`min-height:${height};height:${height}`"
      key-field="id"
      :items="showList"
      :item-size="itemHeight"
      :emit-update="false"
    >
      <template #default="{ index, item }">
        <div class="item">
          <template v-if="!item.warp">
            <span class="linenumber">{{ index + 1 }}</span>
            <span v-html="item.text"></span>
            &nbsp;
          </template>
        </div>
      </template>
    </RecycleScroller>
  </div>
</template>
<script>
import ansiparse from '@/utils/parse-ansi'
// import codeEditor from "@/components/codeEditor";
import { RecycleScroller } from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'
import Prism from 'prismjs'
import 'prismjs/components/prism-log'
import 'prismjs/themes/prism-okaidia.min.css'
export default {
  components: {
    // codeEditor,
    RecycleScroller
  },
  props: {
    height: {
      type: String,
      default: '50vh'
    },

    config: {
      type: Object,
      default: () => {}
    },
    id: {
      type: String,
      default: 'logScrollArea'
    }
  },
  data() {
    return {
      defText: 'loading context...',
      logContext: '',
      dataArray: [],
      idInc: 0,
      visibleStartIndex: -1,
      itemHeight: 24,
      inited: false,
      uniqueId: `component_${Math.random().toString(36).substring(2, 15)}`
    }
  },
  computed: {
    wordBreak() {
      // this.changeBuffer();
      return this.config.wordBreak || false
    },
    showList() {
      const element = document.querySelector(`#${this.uniqueId}`)
      let result
      if (this.inited) {
        result = this.dataArray.length
          ? [...this.dataArray]
          : [
              {
                text: this.defText,
                id: '0-def'
              }
            ]
      } else {
        // 还没有 dom 对象
        result = [
          {
            text: 'loading..................',
            id: '0-def'
          }
        ]
      }
      let warp = false
      if (element) {
        // 填充空白，避免无内容 页面背景太低
        const min = Math.ceil(element.clientHeight / this.itemHeight)
        const le = min - result.length
        for (let i = 0; i < le - 1; i++) {
          result.push({
            id: 'system-warp-empty:' + i,
            warp: true
          })
          warp = true
        }
      }
      if (!warp) {
        // 最后填充一行空白，避免无法看到滚动条
        result = result.concat([
          {
            id: 'system-warp-end:1',
            warp: true
          }
        ])
      }
      return result
    }
    // showContext: {
    //   get() {
    //     return this.logContext || this.defText;
    //   },
    //   set() {},
    // },
  },
  mounted() {
    const timer = setInterval(() => {
      const element = document.querySelector(`#${this.uniqueId}`)
      if (element) {
        this.inited = true
        clearInterval(timer)
      }
    }, 200)
  },
  methods: {
    scrollToBottom() {
      const element = document.querySelector(`#${this.uniqueId}`)
      if (element) {
        this.scrollTo(element.scrollHeight - element.clientHeight)
      }
    },
    scrollToTop() {
      this.scrollTo(0)
    },
    scrollTo(h) {
      const element = document.querySelector(`#${this.uniqueId}`)
      if (element) {
        // console.log(element, element.scrollHeight);
        element.scrollTop = h
        // this.scrollTo(element, element.scrollHeight - element.clientHeight, 500);
        // element.scrollIntoView(false);
      }
    },
    scrollTo2(element, position) {
      if (!window.requestAnimationFrame) {
        window.requestAnimationFrame = function (cb) {
          return setTimeout(cb, 10)
        }
      }
      let scrollTop = element.scrollTop
      const step = function () {
        const distance = position - scrollTop
        scrollTop = scrollTop + distance / 5
        if (Math.abs(distance) < 1) {
          element.scrollTop = position
        } else {
          element.scrollTop = scrollTop
          requestAnimationFrame(step)
        }
      }
      step()
    },
    onUpdate(viewStartIndex, viewEndIndex, visibleStartIndex, visibleEndIndex) {
      const tempArray = this.dataArray.slice(visibleStartIndex, visibleEndIndex)
      this.logContext = tempArray
        .map((item) => {
          return item.text
        })
        .map((item) => {
          return (
            // gitee isuess I657JR
            ansiparse(item)
              .map((ansiItem) => {
                return ansiItem.text
              })
              .join('') + '\r\n'
          )
        })
        .join('')
      this.visibleStartIndex = visibleStartIndex

      // console.log(this.dataArray.length, tempArray.length, visibleStartIndex, visibleEndIndex);
      // console.log(this.logContext);
    },
    //
    appendLine(data) {
      if (!data) {
        return
      }
      const tempArray = (Array.isArray(data) ? data : [data])
        .flatMap((item) => {
          return item.split('\r\n')
        })
        .map((item) => {
          return {
            text: ansiparse(item)
              .map((ansiItem) => {
                return ansiItem.text
              })
              .join(''),
            id: this.idInc++
          }
        })
        .map((item) => {
          return {
            // 制表符号 替换
            text: Prism.highlight(item.text, Prism.languages.log, 'log').replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;'),
            id: item.id
          }
        })
      if (!tempArray.length) {
        return
      }
      this.dataArray = [...this.dataArray, ...tempArray]
      // console.log(this.dataArray.length, this.showList.length);
      if (this.config.logScroll) {
        setTimeout(() => {
          // 延迟触发滚动
          this.$nextTick(() => {
            this.scrollToBottom('.scroller')
          })
        }, 500)
      }
    },

    clearLogCache() {
      this.dataArray = []
      this.scrollToTop()
    }
  }
}
</script>
<style scoped>
.log-view-wrapper {
  background: #292a2b;
  color: #ffb86c;
  padding: 10px;
  box-shadow: inset 0 0 10px 0 #e8e8e8;
  border-radius: 8px;
}
.scroller {
  height: 100%;
  width: 100%;
  font-family: Operator Mono, Source Code Pro, Menlo, Monaco, Consolas, Courier New, monospace;
  position: relative;
  overflow-y: scroll;
}
:deep(.vue-recycle-scroller__item-wrapper) {
  white-space: nowrap;
  position: unset;
}
.linenumber {
  color: #e6e6e6;
  padding-right: 4px;
  opacity: 0.6;
  -webkit-user-select: none; /* Safari */
  -moz-user-select: none;    /* Firefox */
  -ms-user-select: none;     /* Internet Explorer/Edge */
  user-select: none;
}
</style>
