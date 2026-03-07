const http = require('http');
const https = require('https');
const { URL } = require('url');

const links = [
  'https://assets.gitee.com/favicon.ico',
  'https://logo.clearbit.com/pdf24.org',
  'https://logo.clearbit.com/tool.lu',
  'https://logo.clearbit.com/baidu.com',
  'https://logo.clearbit.com/10minutemail.one',
  'https://logo.clearbit.com/2925.com',
  'https://logo.clearbit.com/uiverse.io',
  'https://logo.clearbit.com/haowallpaper.com',
  'https://logo.clearbit.com/steampowered.com',
  'https://logo.clearbit.com/163.com',
  'https://logo.clearbit.com/youtube.com',
  'https://www.youtube.com/favicon.ico',
  'https://p1.music.126.net/tBTNafgjNnTL1KlZMt7lVA==/18885211718935735.jpg',
  'https://ui-avatars.com/api/?name=狗屁文章&background=0D8ABC&color=fff'
];

function checkUrl(url) {
  return new Promise((resolve) => {
    const parsedUrl = new URL(url);
    const client = parsedUrl.protocol === 'https:' ? https : http;
    const req = client.get(parsedUrl, { headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)' } }, (res) => {
      resolve({ url, status: res.statusCode });
    });
    req.on('error', (e) => {
      resolve({ url, status: e.message });
    });
    req.setTimeout(5000, () => {
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
