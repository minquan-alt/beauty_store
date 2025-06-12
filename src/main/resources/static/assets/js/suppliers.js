function editSupplier(id) {
    console.log('Edit supplier with ID:', id);
    // window.location.href = '/supplier/edit/' + id;
}

function deleteSupplier(id) {
    if (confirm('Are you sure you want to delete this supplier?')) {
        // Gửi request DELETE
        console.log("U deleted supplier with ID: ", id);
        // fetch('/supplier/' + id, {
        //     method: 'DELETE',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     }
        // })
        // .then(response => {
        //     if (response.ok) {
        //         // Reload trang hoặc remove row
        //         location.reload();
        //     } else {
        //         alert('Error deleting supplier');
        //     }
        // })
        // .catch(error => {
        //     console.error('Error:', error);
        //     alert('Error deleting supplier');
        // });
    }
}