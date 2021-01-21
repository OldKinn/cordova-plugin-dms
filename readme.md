# dms身份证读卡插件

## 开始读卡

```
document.getElementById('start').addEventListener('click', function() {
    cordova.plugin.dms.startReading(function(data) {
        alert(JSON.stringify(data));
    }, function(message) {
        alert(message);
    });
})
```

## 关闭读卡

```
document.getElementById('end').addEventListener('click', function() {
    cordova.plugin.dms.stopReading(function(data) {
        alert(JSON.stringify(data));
    }, function(message) {
        alert(message);
    });
})
```