
//var baseUrl = 'http://localhost:8088/';

var baseUrl = 'http://192.168.10.195:8088/';

var trans = baseUrl+'sendCoinToView'; //转账接口
var anintran = baseUrl+'transactionFromView'; // 转账接口
var accountList = baseUrl +'accountList';//查询账户列表
var witnessList = baseUrl +'witnessList'; //查询见证人列表
var getBlockToView = baseUrl +'getBlockToView'; //查询账户列表
var getAccountInfo = baseUrl +'queryAccount'; //查询账户详情

var createAssetView = baseUrl + 'createAssetIssueToView' //发行资产
var signView = baseUrl + 'transactionFromView'; //签名接口
