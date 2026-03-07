const http = require('http');
const https = require('https');
const { URL } = require('url');

const links = [
  'https://github.githubassets.com/favicons/favicon.svg',
  'https://gitee.com/favicon.ico',
  'https://static-production.npmjs.com/b0f1a8318363185cc2ea6a40ac23eeb2.png',
  'https://cn.vitejs.dev/logo.svg',
  'https://vuejs.org/logo.svg',
  'https://element-plus.org/images/element-plus-logo-small.svg',
  'https://www.apihz.cn/favicon.ico',
  'https://www.xunjietupian.com/favicon.ico',
  'https://tools.pdf24.org/favicon.ico',
  'https://tool.lu/favicon.ico',
  'https://www.datatool.vip/favicon.ico',
  'https://www.baidu.com/favicon.ico',
  'https://10minutemail.one/favicon.ico',
  'https://www.zfrontier.com/favicon.ico',
  'https://www.kimi.com/favicon.ico',
  'https://www.2925.com/favicon.ico',
  'https://img.alicdn.com/tfs/TB1O_23Bxn1gK0jSZKPXXXvUXXa-32-32.ico',
  'https://uiverse.io/favicon.ico',
  'https://haowallpaper.com/favicon.ico',
  'https://xiaolincoding.com/icon/favicon.ico',
  'https://lf3-cdn-tos.bytescm.com/obj/static/xitu_juejin_web/6c61ae65d1c41ae8221a670fa32d05aa.svg',
  'https://developer.mozilla.org/favicon-48x48.cbbd161b.png',
  'https://www.bilibili.com/favicon.ico',
  'https://leetcode.cn/favicon.ico',
  'https://suulnnka.github.io/BullshitGenerator/favicon.ico',
  'https://store.steampowered.com/favicon.ico',
  'https://s1.music.126.net/style/favicon.ico',
  'https://www.youtube.com/s/desktop/17300c7a/img/favicon.ico'
];

function checkUrl(url) {
  return new Promise((resolve) => {
    const parsedUrl = new URL(url);
    const client = parsedUrl.protocol === 'https:' ? https : http;
    const req = client.get(url, { headers: { 'User-Agent': 'Mozilla/5.0' } }, (res) => {
      resolve({ url, status: res.statusCode });
    });
    req.on('error', (e) => {
      resolve({ url, status: e.message });
    });
    req.setTimeout(3000, () => {
      req.destroy();
      resolve({ url, status: 'timeout' });
    });
  });
}

async function main() {
  const results = await Promise.all(links.map(checkUrl));
  for (const r of results) {
    if (r.status !== 200) {
      console.log('FAILED:', r.url, 'STATUS:', r.status);
    }
  }
}
main();
