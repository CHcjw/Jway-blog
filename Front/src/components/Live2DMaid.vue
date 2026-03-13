<template>
  <div class="live2d-shell" />
</template>

<script setup>
import { onMounted } from 'vue'

const WIDGET_SCRIPT_ID = 'jway-live2d-widget-script'
const MODEL_JSON = '/live2d/hijiki/hijiki.model.json'
const LEFT_PX = '12px'
const BOTTOM_PX = '-45px'

const loadScript = () =>
  new Promise((resolve, reject) => {
    const existing = document.getElementById(WIDGET_SCRIPT_ID)
    if (existing) {
      if (window.L2Dwidget) {
        resolve()
      } else {
        existing.addEventListener('load', () => resolve(), { once: true })
        existing.addEventListener('error', () => reject(new Error('Live2D script load failed')), { once: true })
      }
      return
    }

    const script = document.createElement('script')
    script.id = WIDGET_SCRIPT_ID
    script.src = '/live2d/L2Dwidget.min.js'
    script.async = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Live2D script load failed'))
    document.head.appendChild(script)
  })

const initLive2D = async () => {
  if (window.__jwayLive2DInited) return

  await loadScript()
  if (!window.L2Dwidget) return

  window.L2Dwidget.init({
    model: {
      jsonPath: MODEL_JSON,
      scale: 1
    },
    display: {
      position: 'left',
      width: 180,
      height: 360,
      hOffset: 4,
      vOffset: -55
    },
    mobile: {
      show: true,
      scale: 0.75
    },
    react: {
      opacityDefault: 0.95,
      opacityOnHover: 1
    }
  })

  window.__jwayLive2DInited = true
}

const applyWidgetPosition = () => {
  ;['live2d-widget', 'live2dcanvas'].forEach((id) => {
    const el = document.getElementById(id)
    if (!el) return
    el.style.setProperty('left', LEFT_PX, 'important')
    el.style.setProperty('right', 'auto', 'important')
    el.style.setProperty('bottom', BOTTOM_PX, 'important')
    el.style.setProperty('top', 'auto', 'important')
  })
}

onMounted(() => {
  initLive2D().catch(() => {
    // keep page usable even if local resource loading fails
  })
  setTimeout(applyWidgetPosition, 0)
  setTimeout(applyWidgetPosition, 300)
  setTimeout(applyWidgetPosition, 1000)
})
</script>

<style lang="scss">
#live2d-widget,
#live2dcanvas {
  z-index: 1400 !important;
}

#live2d-widget {
  left: 12px !important;
  right: auto !important;
  bottom: -60px !important;
  top: auto !important;
}

#live2dcanvas {
  left: 12px !important;
  right: auto !important;
  bottom: -60px !important;
  top: auto !important;
}
</style>
