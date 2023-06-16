pragma solidity ^0.8.4;

contract Mortal {
    /* Define variable owner of the type address*/
    address payable owner;

    /* this function is executed at initialization and sets the owner of the contract */
    constructor () {owner = payable(msg.sender);}

    /* Function to recover the funds on the contract */
    function kill() public {if (msg.sender == owner) selfdestruct(owner);}
}
