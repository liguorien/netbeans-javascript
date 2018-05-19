
  function sh0w3M41l(v,n){
                var c = v.split(";");
                var r = "";
                var k = c.length & 1;
                for(var i=c.length-2;i>=0;i--){
                        r += String.fromCharCode(c[i] >> (((i + k) & 1) + 1));
                }
                document.write("<a href='mailto:"+r+"'>"+n+"</a>");
        }

