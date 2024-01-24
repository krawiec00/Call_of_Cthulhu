package com.app.callofcthulhu.model.data


data class Request( var fromUserId: String? = null,
                    var userMail: String? = null,
                    var toUserId: String? = null,
                    var docId: String? = null,
                    var status: String? = null,
                    var cardName: String? = null)


