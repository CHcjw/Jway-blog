const fs = require('fs');

// Patch App.vue
let app = fs.readFileSync('src/App.vue', 'utf8');
app = app.replace(
  '<el-dropdown-item @click="router.push(\'/moments\')"><i class="bi bi-clock-history"></i> 旧时光</el-dropdown-item>',
  '<el-dropdown-item @click="router.push(\'/moments\')"><i class="bi bi-clock-history"></i> 旧时光</el-dropdown-item>\n                      <el-dropdown-item @click="router.push(\'/navigate/website\')"><i class="bi bi-compass"></i> 导航</el-dropdown-item>'
);
app = app.replace(
  '{ label: \'旧时光\', path: \'/moments\', icon: \'bi-clock-history\', colorClass: \'c-time\' }',
  '{ label: \'旧时光\', path: \'/moments\', icon: \'bi-clock-history\', colorClass: \'c-time\' },\n  { label: \'网址导航\', path: \'/navigate/website\', icon: \'bi-compass\', colorClass: \'c-nav\' }'
);
app = app.replace(
  '.c-time { background: linear-gradient(135deg, #cd9cf2 0%, #f6f3ff 100%); color: #333; }',
  '.c-time { background: linear-gradient(135deg, #cd9cf2 0%, #f6f3ff 100%); color: #333; }\n        .c-nav { background: linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%); }'
);
fs.writeFileSync('src/App.vue', app);

// Patch router/index.js
let router = fs.readFileSync('src/router/index.js', 'utf8');
if (!router.includes('Navigate.vue')) {
  router = router.replace(
    'import AdminDashboard from \'../views/AdminDashboard.vue\'',
    'import AdminDashboard from \'../views/AdminDashboard.vue\'\nimport Navigate from \'../views/Navigate.vue\''
  );
  router = router.replace(
    '{ path: \'/skills\', name: \'Skills\', component: Skills }',
    '{ path: \'/skills\', name: \'Skills\', component: Skills },\n  { path: \'/navigate/website\', name: \'Navigate\', component: Navigate }'
  );
  fs.writeFileSync('src/router/index.js', router);
}
