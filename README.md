# XMPPchatroom

*需要include的jar檔是 smack 和 microsoft-translate 的api，都放在lib的資料夾下了，如果路徑有錯的話從那邊改link

1.程式的進入點是Main.java，註解的地方都先不管他。Main.java 主要只做一件事，就是去call LoginGUI.java。
2.LoginGUI.java 是 Login 的 GUI，接受User輸入的 username 和 password，做簡單的判斷之後丟進Client.java。
  *目前我的server架在我宿舍的電腦上，所以host那一欄現在填的是我的主機IP
  
