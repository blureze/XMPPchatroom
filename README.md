# XMPPchatroom

*需要include的jar檔是 smack 和 microsoft-translate 的api，都放在lib的資料夾下了，如果路徑有錯的話從那邊改link

1. 程式的進入點是Main.java，註解的地方都先不管他。Main.java 主要只做一件事，就是去call LoginGUI.java。
2. LoginGUI.java 是 Login 的 GUI，接受User輸入的 username 和 password，做簡單的判斷之後丟進Client.java。
	*目前我的server架在我宿舍的電腦上，所以host那一欄現在填的是我的主機IP
3. Client.java 都是網路上面的code，可以直接拿去用。主要做的是建立與server的連線及處理訊息傳遞的ChatManager和MessengerListener。
	在Client.java 裡面，會call performLogin() 這個method，如果登入成功就會進到chatGUI。 
4. ChatGUI.java 是主要的聊天室介面(雖然現在很醜)，但是在這裡面實做了送出及顯示訊息的部分，也包括了 Highlight 和 Translation 的功能。
	Highlight 用的是 JTextComponent 底下的東西，getSelectedText()可以知道反白的文字是什麼，getSelectionStart()和getSelectionEnd()可以知道這個字的 start index 和 end index，
	在顯示聊天訊息的JTextArea中，設定 MouseEvent 和 Highlighter 就可以完成 Highlighting 的部分；
	Translate 的地方，有寫好一個 Translator.java，只要把要翻譯的字丟進去就可以得到翻譯結果。
	
除了以上提到的檔案之外，其他都是我自己還沒寫好的code，可以不用理他。
