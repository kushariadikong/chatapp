let socket = new WebSocket(`ws://${window.location.host}`);
let id;
let toId;

socket.onopen = function() {
    let params = new URL(window.location).searchParams;
    let obj = {
        '@class': 'com.kenny.javachatapp.uph.vertx.packet.Join',
        name: params.get('name'),
        room: params.get('room')
    };
    
    if (obj.name == null ||
        obj.room == null ||
        obj.name.trim() == '' ||
        obj.room.trim() == '')
    {
        window.location.href='/?error=Name+and+room+are+required.';
        return;
    }
    
    socket.send(JSON.stringify(obj));
};

socket.onmessage = function(evt) {
    if (evt.data instanceof Blob) {
        var reader = new FileReader()
        reader.onload = function() {
            var json = JSON.parse(reader.result)
            processJson(json)
        }
        reader.readAsText(evt.data)
    }
};

function sendMessage() {
    var input = document.querySelector('input[name=message]');
    var text = input.value;
    
    let message = {
        '@class': 'com.kenny.javachatapp.uph.vertx.packet.ConversationMessage',
        fromId: id,
        toId,
        text
    };

    socket.send(JSON.stringify(message));
    input.value = toId = '';
    input.focus();
}

function processJson(json) {
    var jsonClass = json['@class']
    handlers = [
        {
            '@class': 'com.kenny.javachatapp.uph.vertx.packet.LoginNotification',
            handler: loginNotif
        },
        {
            '@class': 'com.kenny.javachatapp.uph.vertx.packet.TextMessage',
            handler: textMessage
        },
        {
            '@class': 'com.kenny.javachatapp.uph.vertx.packet.ConversationMessage',
            handler: textMessage
        },
        {
            '@class': 'com.kenny.javachatapp.uph.vertx.packet.OnlineUsers',
            handler: renderUsers
        },
        {
            '@class': 'com.kenny.javachatapp.uph.vertx.packet.ErrorMessage',
            handler: nameExists
        }
    ]

    let handler = handlers.find(function(x) { 
        return x['@class'] === jsonClass; 
    });
    
    if (handler !== undefined) {
        handler.handler(json)
    }
}

function nameExists(json) {
    window.location.href=`/?error=${json.text}`;
}

function textMessage(json) {
    updateMessages(json);
}

function renderUsers(json) {
    updateUserList(json);
}

function loginNotif(json) {
    id = json.user.id;
}

function scrollToBottom() {
    let messages = document.querySelector('#messages');
    let newMessage = messages.children[messages.children.length - 1];
    let beforeNewMessage = messages.children[messages.children.length - 2];

    let clientHeight = messages.clientHeight;
    let scrollTop = messages.scrollTop;
    let scrollHeight = messages.scrollHeight;

    let newMessageHeight = newMessage ? newMessage.offsetHeight : 0;
    let beforeNewMessageHeight = beforeNewMessage ? beforeNewMessage.offsetHeight : 0;

    if ((scrollTop +
        clientHeight +
        beforeNewMessageHeight +
        newMessageHeight) >= scrollHeight)
    {
        messages.scrollTo(0, scrollHeight);
    }
}

const timeFormat = 'hh:mm a';
let users = document.querySelector('div#users');
let userTemplate = document.querySelector('#user-template');

let updateUserList = function(json) {
    let userArray = json.users;
    
    users.innerHTML = '';
    let sortedUserArrayBaseOnName = userArray.sort((u1, u2) => u1['name'] - u2['name']);

    for(let index in sortedUserArrayBaseOnName) {
        let html = Mustache.render(
            userTemplate.innerHTML,
            { 
                userId: sortedUserArrayBaseOnName[index]['id'],
                name: sortedUserArrayBaseOnName[index]['name'] 
            }
        );

        users.innerHTML += html;
    }

    let userLiArray = users.querySelectorAll('li');
    for (let index = 0; index < userLiArray.length; index += 1) {
        userLiArray[index].addEventListener('click',
        function() {
            chatInput.value = `@${userLiArray[index].innerHTML} `;
            chatInput.focus();
            toId = userLiArray[index].id;
        });
    }
};

let chatInput = document.querySelector('input[name=message]');

let sendBtn = document.querySelector('#message-form > button');
const updateMessages = function(obj) {
    if (!obj) {
        sendBtn.removeAttribute('disabled');
        return;
    }

    let { createdAt, from, toId, text } = obj;
    let textColor;
    
    if (toId == undefined || toId == null) {
        textColor = "black";
    } else {
        textColor = "red";
    }
        

    let formattedTime = moment(createdAt).format(timeFormat);

    const template = document.querySelector('#message-template').innerHTML;
    let html = Mustache.render(template, {
        createdAt: formattedTime,
        from,
        text,
        textColor
    });

    document.querySelector('#messages').innerHTML += html;

    sendBtn.removeAttribute('disabled');

    scrollToBottom();
};

document.querySelector('#message-form')
    .addEventListener('submit', function(evt) {
        evt.preventDefault();

        sendBtn.setAttribute('disabled', 'disabled');

        sendMessage();

        chatInput.value = "";
});
