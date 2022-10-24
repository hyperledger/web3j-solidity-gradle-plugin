pragma solidity ^0.6.0;

contract Mortal {
    /* Define variable owner of the type address*/
    address payable owner;

    /* this function is executed at initialization and sets the owner of the contract */
    constructor() public {owner = msg.sender;}

    /* TODO 
    consider to move to openzepeelin pausable, see context on issue: 
    https://ethereum.stackexchange.com/questions/65872/invalid-type-for-argument-in-function-call-invalid-implicit-conversion-from-add
    */
    /* Function to recover the funds on the contract */
    function kill() public {if (msg.sender == owner) selfdestruct(payable(owner));}
}
