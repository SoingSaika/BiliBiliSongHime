# BiliBiliSongHime
 哔哩哔哩直播点歌姬

## 配置文件config.json
 位于./config/config.json
 
```json
{
  "room": 2871481, // 要监听的房间号 
  "ui": "CMD",
  "connect_timeout": 10, //超时连接时间
  "download_path": "./cache/song/", //歌曲下载路径
  "action_prefix": null, //操作指令前缀
  "music_login_phone": "17665429233",
  "music_login_password": "xg524633032789",
  "action": [ //操作指令
    {
      "action": "NEXT",
      "command": "下一首",
      "enabled": false
    },
    {
      "action": "PERVIOUS",
      "command": "上一首",
      "enabled": false
    },
    {
      "action": "pause",
      "command": "暂停",
      "enabled": false
    },
    {
      "action": "SEARCH",
      "command": "点歌",
      "enabled": true
    }
  ]
}
```