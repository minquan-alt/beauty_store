/* --- helpers & cart total --- */
    let appliedDiscount = 0;
    function formatVND(num){return num.toLocaleString("vi-VN",{style:"currency",currency:"VND"});}
    function updateTotal(){
      let subtotal=0;
      document.querySelectorAll(".product-item").forEach(item=>{
        const qty=+item.dataset.quantity;
        const price=+item.querySelector(".product-price").innerText.replace(/\D/g,"");
        subtotal+=qty*price;
      });
      const total=Math.max(subtotal-appliedDiscount,0);
      document.getElementById("subtotal").innerText=formatVND(subtotal);
      document.getElementById("discount").innerText=`-${formatVND(appliedDiscount)}`;
      document.getElementById("cart-total").innerText=formatVND(total);
    }

    /* coupon */
    document.getElementById("apply-coupon").addEventListener("click",()=>{
      const code=document.getElementById("coupon-code").value.trim().toUpperCase();
      if(code==="SAVE10"){
        appliedDiscount=100000;
        Swal.fire({icon:"success",title:"Code applied","text":"SAVE10 (-₫100,000)",confirmButtonColor:"#000"});
      }else if(code==="SALE50"){
        appliedDiscount=50000;
        Swal.fire({icon:"success",title:"Code applied","text":"SALE50 (-₫50,000)",confirmButtonColor:"#000"});
      }else{
        appliedDiscount=0;
        Swal.fire({icon:"error",title:"Invalid code","text":"Please check your code.",confirmButtonColor:"#000"});
      }
      updateTotal();
    });

    /* autofill account info */
    document.getElementById("use-account-info").addEventListener("change",function(){
      const data={name:"John Doe",email:"johndoe@example.com",phone:"0901234567"};
      document.getElementById("input-name").value=this.checked?data.name:"";
      document.getElementById("input-email").value=this.checked?data.email:"";
      document.getElementById("input-phone").value=this.checked?data.phone:"";
    });

    /* toggle bank info */
    const paymentSelect=document.getElementById("payment-method");
    const bankInfo=document.getElementById("bank-info");
    paymentSelect.addEventListener("change",()=>bankInfo.style.display=paymentSelect.value==="bank"?"block":"none");

    /* checkout validation */
    document.getElementById("checkout-btn").addEventListener("click",()=>{
      const name=document.getElementById("input-name").value.trim();
      const email=document.getElementById("input-email").value.trim();
      const phone=document.getElementById("input-phone").value.trim();
      const street=document.getElementById("input-street").value.trim();
      const province=document.getElementById("province").value;
      const district=document.getElementById("district").value;
      const ward=document.getElementById("ward").value;
      const method=paymentSelect.value;
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const phoneRegex = /^(0|\+84)(\d{9})$/;

if (!name || !email || !phone || !street) {
  Swal.fire({ icon: "warning", title: "Missing info", text: "Please fill in all required fields.", confirmButtonColor: "#000" });
  return;
}

if (!emailRegex.test(email)) {
  Swal.fire({ icon: "warning", title: "Invalid Email", text: "Please enter a valid email address.", confirmButtonColor: "#000" });
  return;
}

if (!phoneRegex.test(phone)) {
  Swal.fire({ icon: "warning", title: "Invalid Phone", text: "Phone number must start with 0 or +84 and be 10 digits.", confirmButtonColor: "#000" });
  return;
}

if (!street || !province || !district || !ward) {
  Swal.fire({ icon: "warning", title: "Missing address", text: "Please select street, province, district and ward.", confirmButtonColor: "#000" });
  return;
}

if (method === "Select method") {
  Swal.fire({ icon: "warning", title: "Select payment", text: "Please choose a payment method.", confirmButtonColor: "#000" });
  return;
}


      let success="Your order will be paid on delivery. Thank you!";
      if(method==="bank") success="Your order will be confirmed once the payment is successfully received";
      if(method==="momo") success="You will be redirected to Momo to complete payment.";

      Swal.fire({icon:"success",title:"Order placed",text:success,confirmButtonColor:"#000"});
    });

    /* province / district / ward API (unchanged, still uses VN open-api) */
    const provinceSel=document.getElementById("province");
    const districtSel=document.getElementById("district");
    const wardSel=document.getElementById("ward");

    fetch("https://provinces.open-api.vn/api/p/")
      .then(res=>res.json())
      .then(data=>{
        data.forEach(p=>{
          const o=document.createElement("option");
          o.value=p.code;o.textContent=p.name;provinceSel.appendChild(o);
        });
      });

    provinceSel.addEventListener("change",()=>{
      const code=provinceSel.value;
      districtSel.innerHTML='<option value=\"\">Select district</option>';
      wardSel.innerHTML='<option value=\"\">Select ward</option>';
      wardSel.disabled=true;
      if(!code){districtSel.disabled=true;return;}
      districtSel.disabled=false;
      fetch(`https://provinces.open-api.vn/api/p/${code}?depth=2`)
        .then(r=>r.json())
        .then(d=>{
          d.districts.forEach(dist=>{
            const o=document.createElement("option");
            o.value=dist.code;o.textContent=dist.name;districtSel.appendChild(o);
          });
        });
    });

    districtSel.addEventListener("change",()=>{
      const code=districtSel.value;
      wardSel.innerHTML='<option value=\"\">Select ward</option>';
      if(!code){wardSel.disabled=true;return;}
      wardSel.disabled=false;
      fetch(`https://provinces.open-api.vn/api/d/${code}?depth=2`)
        .then(r=>r.json())
        .then(d=>{
          d.wards.forEach(w=>{
            const o=document.createElement("option");
            o.value=w.code;o.textContent=w.name;wardSel.appendChild(o);
          });
        });
    });

    /* initial total */
    updateTotal();
    function copyToClipboard(elementId) {
    const text = document.getElementById(elementId).innerText.trim();
    navigator.clipboard.writeText(text).catch(() => {});
  }

  function copyNote() {
    const orderId = document.getElementById("order-id-note").innerText.trim();
    const fullNote = `Payment for Order ${orderId}`;
    navigator.clipboard.writeText(fullNote).catch(() => {});
  }

  // Khi cập nhật Order ID
  const orderId = "#12345"; // ví dụ
  document.getElementById("order-id").innerText = orderId;
  document.getElementById("order-id-note").innerText = orderId;